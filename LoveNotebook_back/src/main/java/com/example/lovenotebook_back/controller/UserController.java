package com.example.lovenotebook_back.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.lovenotebook_back.common.Constants;
import com.example.lovenotebook_back.controller.vo.WechatLoginVO;
import com.example.lovenotebook_back.entity.User;
import com.example.lovenotebook_back.service.UserService;
import com.example.lovenotebook_back.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户相关的接口", description = "请求接口必须携带token")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录前校验token,未过期不用登录
     *
     * @param token: 唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/16 12:21
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "登录前校验token,返回用户信息", notes = "登录前校验token,返回用户信息")
    @GetMapping("/beforeLogin")
    public Info beforeLogin(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);
        return new Info(true, user);
    }

    /**
     * 用户退出
     *
     * @param token: 唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/16 12:21
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "用户退出", notes = "用户退出")
    @GetMapping("/logout")
    public Info logout(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        JedisUtils.del("token:" + user.getUserId());
        return new Info(true, "退出成功");
    }

    /**
     * 微信授权登录
     *
     * @param wechatLoginVO: 微信登录Bo
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/16 12:18
     */
    @ApiImplicitParam(name = "wechatLoginVO", value = "微信登录基本信息", dataType = "微信登录VO", required = true)
    @ApiOperation(value = "微信授权登录", notes = "微信授权登录")
    @PostMapping("/wechatLogin")
    public Info wechatLogin(@RequestBody WechatLoginVO wechatLoginVO) throws Exception {
        String code = wechatLoginVO.getCode();
        String appId = Constants.WECHAT_APPID;
        String appSecret = Constants.WECHAT_APP_SECRET;
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" +
                appSecret + "&js_code=" + code + "&grant_type=authorization_code";

        // 向微信接口发起请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        // 请求成功会返回对应信息，解析为json
        if (response.isSuccessful()) {
            String body = response.body().string();
            System.out.println(body);
            // 解析JSON获取openid，session_key
            JSONObject jsonObject = JSONObject.parseObject(body);
            String session_key = jsonObject.getString("session_key");
            String openid = jsonObject.getString("openid");

            User user = new User();
            user.setUserSessionKey(session_key);

            HashMap<String, Object> map = new HashMap<>();
            Thread.sleep(1000);

            // 判断数据中有无当前用户
            User userInfo = userService.selectOneByOpenId(openid);

            // 无当前用户，写入数据库
            if (userInfo == null) {
                if (wechatLoginVO.getNickName().equals("") || wechatLoginVO.getAvatar().equals("")) {
                    return new Info(false, "授权失败，请重试");
                }

                // 雪花算法生成ID
                SnowflakeIdUtils idWorker = new SnowflakeIdUtils(1, 2);
                long id = idWorker.nextId();

                user.setUserId(id);
                user.setUserSex(wechatLoginVO.getSex());

                String date = DateUtils.formatDate(new Date(), "YYYY-MM-dd");
                user.setUserRegisterDate(DateUtils.toDate(date));

                user.setUserIsLove(0);
                user.setUserOpenId(openid);
                user.setUserNickName(wechatLoginVO.getNickName());
                user.setUserAvatar(wechatLoginVO.getAvatar());
                user.setUserLever(0);
                user.setUserHeartCount(50);

                boolean b = userService.addUser(user);
                if (b) {
                    userInfo = userService.selectOneByOpenId(openid);
                } else {
                    return new Info(false, "授权失败，请重试");
                }
            }

            // 判断是否恋爱
            if (userInfo.getUserIsLove() == 1) {
                userInfo.setLover(userService.getById(userInfo.getUserLoveId()));
            }

            // 下发token
            String token = JwtHelper.generateToken("user", userInfo);
            // 将token写入Redis
            JedisUtils.set("token:" + userInfo.getUserId(), token, Constants.REDIS_CACHE_SECONDS);

            map.put("token", token);
            return new Info(true, map, "授权成功");
        }
        return new Info(false, "授权失败，请重试");
    }

    /**
     * 用户更新头像
     *
     * @param token:      唯一凭证
     * @param userAvatar: 图片地址
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/17 15:23
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "userAvatar", value = "头像图片地址", dataType = "string", required = true)
    })
    @ApiOperation(value = "用户更新头像", notes = "用户更新头像")
    @PostMapping("/userUpdateAvatar")
    public Info userUpdateAvatar(@RequestHeader String token, @RequestBody String userAvatar) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        User jsonUser = mapper.readValue(userAvatar, User.class);

        User user = JwtHelper.getUserByToken(token);

        user.setUserAvatar(jsonUser.getUserAvatar());

        boolean b = userService.updateById(user);
        if (b) {
            //上传成功，重新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "用户更新头像成功");
        } else {
            return new Info(false, "用户更新头像失败");
        }

    }

    /**
     * 用户更新昵称
     *
     * @param token:        唯一凭证
     * @param userNickName: 昵称
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/17 15:23
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "userNickName", value = "昵称", dataType = "string", required = true)
    })
    @ApiOperation(value = "用户更新昵称", notes = "用户更新昵称")
    @PostMapping("/userUpdateNickname")
    public Info userUpdateNickname(@RequestHeader String token, @RequestBody String userNickName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        User jsonUser = mapper.readValue(userNickName, User.class);

        User user = JwtHelper.getUserByToken(token);

        user.setUserNickName(jsonUser.getUserNickName());

        boolean b = userService.updateById(user);
        if (b) {
            //上传成功，重新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "用户更新昵称成功");
        } else {
            return new Info(false, "用户更新昵称失败");
        }
    }

    /**
     * 用户更新性别
     *
     * @param token:   唯一凭证
     * @param userSex: 性别
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/17 15:23
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "userSex", value = "性别：0-未设置 1-男 2-女", dataType = "int", required = true)
    })
    @ApiOperation(value = "用户更新性别", notes = "用户更新性别")
    @PostMapping("/userUpdateSex/{userSex}")
    public Info userUpdateSex(@RequestHeader String token, @PathVariable int userSex) {
        User user = JwtHelper.getUserByToken(token);

        user.setUserSex(userSex);

        boolean b = userService.updateById(user);
        if (b) {
            //上传成功，重新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "用户更新性别成功");
        } else {
            return new Info(false, "用户更新性别失败");
        }
    }

    /**
     * 用户解除恋爱关系
     *
     * @param token: 唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/17 15:23
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "用户解除恋爱关系", notes = "用户解除恋爱关系")
    @PostMapping("/userDissolvesLoveRelationship")
    public Info userDissolvesLoveRelationship(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        // 修改当前用户信息
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("user_is_love", 0);
        updateWrapper.set("user_love_date", null);
        updateWrapper.set("user_love_id", null);
        updateWrapper.eq("user_id", user.getUserId());

        boolean b = userService.update(updateWrapper);

        // 修改当前用户恋人信息
        UpdateWrapper<User> loverUpdateWrapper = new UpdateWrapper<>();
        loverUpdateWrapper.set("user_is_love", 0);
        loverUpdateWrapper.set("user_love_date", null);
        loverUpdateWrapper.set("user_love_id", null);
        loverUpdateWrapper.eq("user_id", user.getLover().getUserId());

        boolean bb = userService.update(loverUpdateWrapper);
        if (b && bb) {
            user.setLover(null);
            user.setUserIsLove(0);
            user.setUserLoveDate(null);
            user.setUserLoveId(null);
            // 上传成功，重新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "用户解除恋爱关系成功");
        } else {
            return new Info(false, "用户解除恋爱关系失败");
        }
    }

    /**
     * 用户绑定恋人
     *
     * @param token:      唯一凭证
     * @param userLoveId: 恋人id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/18 16:15
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "userLoveId", value = "用户恋人id", dataType = "long", required = true)
    })
    @ApiOperation(value = "用户绑定恋人", notes = "用户绑定恋人")
    @PostMapping("/userBindsLover/{userLoveId}")
    public Info userBindsLover(@RequestHeader String token, @PathVariable long userLoveId) throws Exception {
        User user = JwtHelper.getUserByToken(token);

        // 校验恋人id是否存在
        User lover = userService.getById(userLoveId);
        if (lover == null) {
            return new Info(false, "Id不存在!");
        }

        // 修改当前用户信息
        String date = DateUtils.formatDate(new Date(), "YYYY-MM-dd");
        user.setUserIsLove(1);
        user.setUserLoveDate(DateUtils.toDate(date));
        user.setUserLoveId(userLoveId);

        boolean b = userService.updateById(user);

        // 修改当前用户恋人信息
        lover.setUserIsLove(1);
        lover.setUserLoveDate(DateUtils.toDate(date));
        lover.setUserLoveId(user.getUserId());

        boolean bb = userService.updateById(lover);
        if (b && bb) {
            user.setLover(lover);
            // 新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "用户绑定恋人成功");
        } else {
            return new Info(false, "用户绑定恋人失败");
        }
    }

    /**
     * @param token 唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @description: 增加用户爱心数量（目前随机获取，后期可能会修改获取爱心方式）
     * @author sun0316
     * @date 2023/7/6 0:09
     */
    @PostMapping("/userAddHeart")
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "增加用户爱心数量", notes = "目前随机获取，后期可能会修改获取爱心方式")
    public Info userAddHeart(@RequestHeader String token) {
        Random random = new Random(100);
        int randomNum = random.nextInt(10) + 1; //[1,11)

        User user = JwtHelper.getUserByToken(token);
        user.setUserHeartCount(user.getUserHeartCount() + randomNum);

        boolean b = userService.updateById(user);

        if (b) {
            // 上传成功，重新下发token
            String newToken = JwtHelper.generateToken("user", user);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);
            return new Info(true, map, "增加用户爱心数量成功");
        }
        return new Info(false, "增加用户爱心数量失败");
    }
}
