<template>
	<view>
		<BackHeader title="修改餐厅名字"></BackHeader>
		<view class="content">
			<view>名字：</view>
			<input type="text" class="weui-input" v-model="name" placeholder="换个新名字吧(不支持特殊字符)" />

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
				name: ''
			};
		},
		methods: {
			onSubmit() {
				if (this.name === '') {
					uni.showToast({
						icon: 'none',
						title: '请输入名字'
					})
					return false;
				}
				console.log(this.name);
				// 这里做自己的存储逻辑
				var that = this;
				var restaurant = storage.get("restaurantInfo");
				restaurant.restaurantName = that.name
				let pages = getCurrentPages();
				let beforePage = pages[pages.length - 2];
				that.$http.httpPostByToken("/restaurant/updateRestaurantName", restaurant).then(res => {
					if (res.data.flag == true) {
						uni.navigateBack({
							success: () => {
								beforePage.$vm.checkIsRestaurant(); // 执行前一个页面的刷新
							}
						});
					}
				});
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