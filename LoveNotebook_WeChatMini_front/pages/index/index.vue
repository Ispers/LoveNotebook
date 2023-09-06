<template>
	<view>
		<MainHeader title="恋恋笔记本"></MainHeader>
		<!-- 正文内容 -->
		<view class="content">
			<!-- 顶部头像展示 -->
			<view class="top">
				<IsLove :user="user" v-if="user.userIsLove == 1"></IsLove>
				<NoLove @init="init()" :user="user" v-if="user.userIsLove == 0"></NoLove>
			</view>

			<!-- 中间 -->
			<view class="mid">

			</view>

		</view>

		<MyTabBar></MyTabBar>
	</view>
</template>

<script>
	import MainHeader from "../../components/main_header.vue"
	import MyTabBar from "../../components/my_tabBar.vue"
	import IsLove from "../../components/index/index_top_islove.vue"
	import NoLove from "../../components/index/index_top_nolove.vue"
	import storage from "../../utils/storage"

	export default {
		components: {
			MainHeader,
			MyTabBar,
			IsLove,
			NoLove
		},
		data() {
			return {
				user: {
					userId: 0,
					userSex: 0,
					userRegisterDate: "",
					userIsLove: 0,
					userLoveDate: "",
					userNickName: "",
					userAvatar: "",
					userLoveId: 0,
					lover: {
						userId: 0,
						userSex: 0,
						userRegisterDate: "",
						userIsLove: 0,
						userLoveDate: "",
						userNickName: "",
						userAvatar: "",
						userLoveId: 0,
					}
				}
			};
		},
		onLoad() {

		},
		onShow() {
			this.init()
		},
		onPullDownRefresh() {
			//刷新数据之后停止刷新效果
			uni.stopPullDownRefresh()
		},
		created() {
			var token = storage.getToken();
			if (token != null && token != '') {
				this.beforeLogin(token);
			} else {
				uni.showModal({
					title: '温馨提示',
					content: '亲，授权微信登录后才能正常使用小程序功能',
					success: (res) => {
						if (res.confirm) {
							this.wechatLogin();
						} else {
							uni.showToast({
								title: '您拒绝了授权请求',
								icon: 'error',
								duration: 2000
							});
						}
					}
				})
			}
		},
		methods: {
			init() {
				var userinfo = storage.get("userinfo");
				this.user = userinfo;
			},
			beforeLogin(token) {
				var that = this;
				this.$http.httpGetByToken('/user/beforeLogin').then((res) => {
					console.log(res);
					if (res.data.flag) {
						storage.set("userinfo", res.data.data)
						that.init()
					} else {
						uni.showModal({
							title: '温馨提示',
							content: '亲，登录已过期请重新登录',
							success: (res) => {
								if (res.confirm) {
									that.wechatLogin();
								} else {
									uni.showToast({
										title: '您拒绝了授权请求',
										icon: 'error',
										duration: 2000
									});
								}
							}
						})
					}
				})
			},
			wechatLogin() {
				var that = this;
				let code = ''
				uni.login({
					provider: 'weixin',
					success: function(res) {
						code = res.code;
						console.log('login重新登录', {
							code: res.code,
							codeinfo: res
						});
					},
					fail: function(res) {
						console.log(res)
					}
				});

				// 获取用户信息
				uni.getUserProfile({
					desc: '获取您的昵称、头像及性别',
					success: (res) => {
						console.log("成功", res);
						that.$http.httpPost('/user/wechatLogin', {
							code: code,
							nickName: res.userInfo.nickName,
							avatar: res.userInfo.avatarUrl,
							sex: res.userInfo.gender
						}).then((res) => {
							console.log(res);
							if (res.data.flag) {
								storage.setToken(res.data.data.token);
								that.beforeLogin(storage.getToken());
							} else {
								uni.showModal({
									title: '登录失败',
									content: '请刷新小程序后重新操作',
								});
							}
						})
					}
				});

			},
		}
	}
</script>

<style lang="scss" scoped src="./index.scss"></style>