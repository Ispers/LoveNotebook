<template>
	<view class="main">
		<view class="left">
			<u-avatar :src="user.userAvatar" size="large"></u-avatar>
			<view>{{user.userNickName}}</view>
		</view>

		<view class="middle">
			点击加号邀请你的另一半吧
		</view>

		<view class="right">
			<button type="balanced" open-type="" @click="addLover()">
				<image :src="src"></image>
			</button>
			<view>Ta</view>
		</view>



		<u-mask :show="show" @click="show = false">
			<view class="warp">
				<view class="rect" @tap.stop>
					<view class="text">
						赶快邀请你的另一半加入吧!
					</view>
					<view class="input">
						<u-input v-model="loveruserId" type="text" :border="true" placeholder="请输入Ta的love笔记本ID" />
					</view>
					<view class="button">
						<u-button type="primary" @click="bSubmit()">确定</u-button>
					</view>
				</view>
			</view>
		</u-mask>

		<u-toast ref="uToast" />
	</view>
</template>

<script>
	import storage from "../../utils/storage"

	export default {
		props: {
			user: {
				type: Object,
				default: function() {
					return {
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
				}
			}
		},
		data() {
			return {
				src: "/static/img/plus_w.png",
				show: false,
				loveruserId: null
			}
		},
		methods: {
			addLover() {
				this.show = true;
			},
			bSubmit() {
				var that = this;
				this.$http.httpPostByToken('/user/userBindsLover/' + this.loveruserId).then(res => {
					console.log(res);
					if (res.data.data.flag == false) {
						that.$refs.uToast.show({
							title: '输入ID不正确！',
							type: 'error'
						})
					} else {
						storage.setToken(res.data.data.token);

						that.$http.httpGetByToken('/user/beforeLogin').then(res => {
							console.log(res);
							storage.set("userinfo", res.data.data);
							this.$emit('init');
						})
					}
				})
			}
		}
	}
</script>

<style lang="scss" scoped src="./scss/index_top_nolove.scss">




</style>