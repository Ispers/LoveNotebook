import http, { httpGetByToken, httpPostByToken } from './http.js'
import storage from "./storage.js"

export default {
	/**
	 * 根据token获取用户信息,将用户信息存到缓存
	 * @param {Object} token
	 */
	getUserinfoByToken(token){
		httpGetByToken('/user/getUserinfo').then(res=>{
			console.log(res);
			if(res.data.flag){
				storage.set("userinfo",res.data.data)
				return true;
			}else{
				return false;
			}
		})
	},

	/**
	 * rpx 转换为 px ，传参类型是数字（Number）
	 */
	rpxTopx(rpx) {
	 
	  let deviceWidth = wx.getSystemInfoSync().windowWidth; //获取设备屏幕宽度
	 
	  let px = (deviceWidth / 750) * Number(rpx)
	 
	  return Math.floor(px);
	 
	},
	
	/**
	 * px 转换为 rpx ，传参类型是数字（Number）
	 */
	pxTorpx(px) {
	 
	  let deviceWidth = wx.getSystemInfoSync().windowWidth; //获取设备屏幕宽度
	 
	  let rpx = (750 / deviceWidth) * Number(px)
	 
	  return Math.floor(rpx);
	 
	}
};