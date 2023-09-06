<template>
	<view>
		<BackHeader title=" "></BackHeader>
		<view class="content">
			<view class="top">
				<view class="left">√</view>
				<view class="top_text">下单成功</view>
			</view>

			<view class="mid">
				分享给你的专属老板，让Ta为你准备大餐吧！
			</view>

			<!-- <view class="btn">提醒老板</view> -->
			<button open-type="share" class="btn">提醒老板</button>
		</view>
	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import storage from "../../utils/storage";

	export default {
		components: {
			BackHeader
		},
		data() {
			return {

			};
		},
		onShareAppMessage(res) {
			if (res.from === 'button') { // 来自页面内分享按钮
				console.log(res.target)
			}
			return {
				title: '你亲爱的顾客下单啦',
				path: '/pages/me/me'
			}
		},
		onLoad() {

		},
		methods: {
			init() {

			}
		}
	}
</script>

<style lang="scss" scoped>
	.content {
		background-color: #fff;
		width: 100%;
		height: 400rpx;
	}

	.top {
		display: flex;
		// border: 1px solid #000;
		padding-left: 35%;
		padding-top: 27rpx;
		height: 100rpx;

		.left {
			// border: 1px solid #000;
			border-radius: 50%;
			background-color: #ffc0cb;
			color: #fff;
			height: 40rpx;
			width: 40rpx;
			text-align: center;
			line-height: 40rpx;
			margin-top: 7rpx;
			margin-right: 15rpx;
		}

		.top_text {
			// border: 1px solid #000;
			font-size: 35rpx;
			font-weight: bold;
			color: #000;
		}
	}

	.mid {
		color: #000;
		font-size: 32rpx;
		margin-left: 10%;
	}

	.btn {
		background-color: #ffc0cb;
		border-radius: 50rpx;
		color: #fff;
		width: 500rpx;
		height: 90rpx;
		text-align: center;
		line-height: 90rpx;
		margin-left: 17%;
		margin-top: 70rpx;
	}
</style>