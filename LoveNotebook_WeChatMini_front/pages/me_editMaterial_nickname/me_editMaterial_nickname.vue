<template>
	<view>
		<BackHeader title="修改昵称"></BackHeader>
		<view class="content">
			<view>昵称：</view>
			<input type="nickname" class="weui-input" :value="nickName" @blur="bindblur" placeholder="换个新名字吧(不支持特殊字符)"
				@input="bindinput" />

			<u-button type="primary" @click="onSubmit">保存</u-button>
		</view>
	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import common from "../../utils/common";
	import storage from "../../utils/storage";

	export default {
		components: {
			BackHeader
		},
		data() {
			return {
				nickName: ''
			};
		},
		methods: {
			bindblur(e) {
				this.nickName = e.detail.value; // 获取微信昵称
			},
			bindinput(e) {
				this.nickName = e.detail.value; //这里要注意如果只用blur方法的话用户在输入玩昵称后直接点击保存按钮，会出现修改不成功的情况。
			},
			onSubmit() {
				if (this.nickName === '') {
					uni.showToast({
						icon: 'none',
						title: '请输入昵称'
					})
					return false;
				}
				console.log(this.nickName);
				// 这里做自己的存储逻辑
				var that = this;
				this.$http.httpPostByToken('/user/userUpdateNickname', {
					userNickName: this.nickName
				}).then(res => {
					console.log(res);
					storage.setToken(res.data.data.token);

					that.$http.httpGetByToken('/user/beforeLogin').then(res => {
						console.log(res);
						storage.set("userinfo", res.data.data)

						uni.navigateBack();
					})
				}, err => {
					console.log(err);
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content {
		display: flex;
		background-color: #fff;
		height: 100rpx;
		line-height: 100rpx;

		view {
			width: 160rpx;
			padding-left: 40rpx;
		}

		.weui-input {
			height: 100rpx;
			width: 400rpx;
		}

		u-button {
			margin-left: 3%;
			margin-top: 10rpx;
		}
	}
</style>