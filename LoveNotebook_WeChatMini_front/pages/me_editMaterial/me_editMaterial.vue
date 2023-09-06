<template>
	<view>
		<BackHeader title="编辑资料"></BackHeader>

		<view class="content" v-model="user">

			<view class="avatar">
				<view class="atitle">头像</view>
				<button type="balanced" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
					<image :src="user.src" class="refreshIcon"></image>
				</button>
				<img class="aimg2" :src="right">
				<view class="aline"></view>
			</view>


			<view class="other" @click="gotoNickname()">
				<view class="title">昵称</view>
				<view class="text">{{user.nickname}}</view>
				<img class="img2" :src="right">
				<view class="line"></view>
			</view>

			<view class="other" @click="gotoSex()">
				<view class="title">性别</view>
				<view class="text">{{user.sex == 0 ? "未填写" : (user.sex == 1 ? "男" : "女")}}</view>
				<img class="img2" :src="right">
				<view class="line"></view>
			</view>

			<view class="other" v-if="user.isLove != 0" @click="dissolveRelationship()">
				<view class="title" style="color: #ff0000;">解除恋爱关系</view>
				<img class="img2" :src="right">
				<view class="line"></view>
			</view>


		</view>
	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import config from "@/config";
	import storage from "../../utils/storage";
	import common from "../../utils/common.js";

	export default {
		components: {
			BackHeader
		},
		data() {
			return {
				user: {
					src: "/static/img/test/avatar/sun.jpg",
					nickname: "听橘子海.",
					sex: 1,
					isLove: 0
				},
				right: "/static/img/me_menu/arrow_right.png"
			};
		},
		onLoad() {
			this.init();
		},
		onShow() {
			this.init();
		},
		methods: {
			init() {
				var userinfo = storage.get("userinfo");
				this.user.nickname = userinfo.userNickName;
				this.user.src = userinfo.userAvatar;
				this.user.sex = userinfo.userSex;
				this.user.isLove = userinfo.userIsLove;
			},
			onChooseAvatar(e) {
				console.log(e);
				let self = this;
				let {
					avatarUrl
				} = e.detail;
				uni.showLoading({
					title: '加载中'
				});
				this.user.src = avatarUrl

				uni.uploadFile({
					url: config.uni_app_web_api_url + '/upload/file',
					filePath: avatarUrl,
					name: 'file',
					header: {
						"Content-Type": "multipart/form-data",
						"token": storage.getToken()
					},
					success: (e) => {
						//写数据库
						self.$http.httpPostByToken("/user/userUpdateAvatar", {
							"userAvatar": config.uni_app_web_url + e.data
						}).then(res => {
							console.log(res);
							storage.setToken(res.data.data.token);
							self.$http.httpGetByToken('/user/beforeLogin').then(res => {
								storage.set("userinfo", res.data.data)
							})
						})
					},
					fail: (error) => {
						uni.showToast({
							title: error,
							duration: 2000
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},
			gotoNickname() {
				uni.navigateTo({
					url: "../me_editMaterial_nickname/me_editMaterial_nickname"
				})
			},
			gotoSex() {
				var that = this;
				uni.showActionSheet({
					alertText: '请选择性别',
					itemList: ['男', '女'],
					// 字体颜色
					itemColor: "#007aff",
					success(res) {
						// 选择其中任意一项后，获取其索引（res.tapIndex），从0开始
						console.log(res.tapIndex)
						that.$http.httpPostByToken('/user/userUpdateSex/' + (res.tapIndex + 1)).then(res => {
							console.log(res);
							storage.setToken(res.data.data.token);

							that.$http.httpGetByToken('/user/beforeLogin').then(res => {
								console.log(res);
								storage.set("userinfo", res.data.data);
								that.init();
							})
						})
					}
				})
			},
			dissolveRelationship() {
				var that = this;
				uni.showModal({
					title: '提示',
					// 提示文字
					content: '确认解除恋爱关系吗？',
					// 取消按钮的文字自定义
					cancelText: "取消",
					// 确认按钮的文字自定义
					confirmText: "解除",
					//删除字体的颜色
					confirmColor: 'red',
					//取消字体的颜色
					cancelColor: '#000000',
					success: function(res) {
						if (res.confirm) {
							// 执行确认后的操作
							that.$http.httpPostByToken('/user/userDissolvesLoveRelationship').then(res => {
								console.log(res);
								storage.setToken(res.data.data.token);

								that.$http.httpGetByToken('/user/beforeLogin').then(res => {
									console.log(res);
									storage.set("userinfo", res.data.data);
									storage.remove("allFoodInfo");
									storage.remove("restaurantInfo");
									that.init();
								})
							})
						}
					}
				})
			}
		}
	}
</script>

<style lang="scss" scoped src="./me_editMaterial.scss"></style>