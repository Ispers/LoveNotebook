<template>
	<view>
		<BackHeader title="订单结算"></BackHeader>

		<view class="top">
			<view class="top_left">
				<image :src="userInfo.userAvatar"></image>
				<view>{{userInfo.userNickName}}</view>
			</view>

			<view class="top_middle">{{restaurantInfo.restaurantName}}</view>

			<view class="top_right">
				<image :src="userInfo.lover.userAvatar"></image>
				<view>{{userInfo.lover.userNickName}}</view>
			</view>

		</view>
		<!-- <view style="height: 260rpx;"></view> -->

		<scroll-view class="main" scroll-y :style="'height:' + scrollHeight + 'px;'">
			<view style="background-color: #fff;">

				<view class="main_item" v-for="(item, index) in cartList">
					<image :src="item.food.foodImg"></image>

					<view class="main_item_name">
						<view class="name">{{item.food.foodName}}</view>
						<view class="num">x&nbsp;{{item.cartFoodCount}}</view>
					</view>

					<view class="main_item_price">
						<image src="../../static/img/heart.png"></image>
						<view>{{item.food.foodHeartCount}}</view>
					</view>
				</view>

				<view class="now_heart">
					<view class="text">剩余爱心值</view>
					<image src="../../static/img/heart.png"></image>
					<view class="num">{{userInfo.userHeartCount}}</view>
				</view>

				<view class="use_heart">
					<view class="text">需消耗</view>
					<image src="../../static/img/heart.png"></image>
					<view class="num">{{getUseHeart()}}</view>
				</view>

				<view class="love_word">
					<view class="title">一句情话</view>
					<input type="text" v-model="orderVO.orderTitle" placeholder="送给Ta一句温暖的话吧!">
				</view>

				<view class="love_notes">
					<view class="title">备注</view>
					<input type="text" v-model="orderVO.orderNote" placeholder="还有什么要交代的吗">
				</view>

			</view>
		</scroll-view>

		<view class="bottom">
			<view class="bottom_text">剩余：</view>
			<image src="../../static/img/heart.png"></image>
			<view class="bottom_price">
			{{userInfo.userHeartCount - getUseHeart() <= 0 ? '余额不足' : (userInfo.userHeartCount - getUseHeart())}}
			</view>
			<view class="bottom_btn" @click="submitOrder()">提交订单</view>
		</view>

	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import storage from "../../utils/storage";
	import common from "../../utils/common.js"

	export default {
		components: {
			BackHeader
		},
		data() {
			return {
				userInfo: {},
				restaurantInfo: {},
				cartList: [],
				scrollHeight: 400,
				orderVO: {
					orderNote: "",
					orderTitle: "",
					foodList: []
				}
			};
		},
		onLoad() {
			this.getClineHeight();
			this.init();
		},
		onUnload() {
			storage.remove("cartList");
		},
		methods: {
			init() {
				this.userInfo = storage.get('userinfo');
				this.restaurantInfo = storage.get('restaurantInfo');
				this.cartList = storage.get("cartList");
			},
			submitOrder() {
				var that = this;
				
				if(this.userInfo.userHeartCount - this.getUseHeart() <= 0){
					uni.showToast({
						title: '您的爱心余额不足',
						icon: 'none',
						duration: 1500
					})
					return;
				}
				
				this.orderVO.orderUserId = this.userInfo.userId
				this.orderVO.orderTotalFoodHeartCount = this.getUseHeart();
				this.orderVO.orderTotalFoodTypeCount = this.cartList.length;
				for (var i = 0; i < this.cartList.length; i++) {
					this.orderVO.foodList.push({
						foodId: this.cartList[i].food.foodId,
						foodName: this.cartList[i].food.foodName,
						foodImg: this.cartList[i].food.foodImg,
						foodHeartCount: this.cartList[i].food.foodHeartCount,
						cartCount: this.cartList[i].cartFoodCount,
						foodSellCount: this.cartList[i].food.foodSellCount
					})
				}
				console.log(this.orderVO)
				this.$http.httpPostByToken("/order/submitOrder", this.orderVO).then(res => {
					console.log(res);
					storage.setToken(res.data.data.token);

					that.$http.httpGetByToken('/user/beforeLogin').then(res => {
						storage.set("userinfo", res.data.data);
						uni.redirectTo({
							url: "../restaurant_order_success/restaurant_order_success"
						})
					})
				})
			},
			getUseHeart() {
				var cartList = this.cartList;
				var useHeart = 0;
				for (var i = 0; i < cartList.length; i++) {
					useHeart += cartList[i].cartFoodCount * cartList[i].food.foodHeartCount
				}
				return useHeart;
			},
			getscrollHeight() {
				const res = uni.getSystemInfoSync()
				if (res.platform === 'ios') {
					return 44 + res.statusBarHeight + common.rpxTopx(150) + common.rpxTopx(262)
				} else if (res.platform === 'android') {
					return 48 + res.statusBarHeight + common.rpxTopx(150) + common.rpxTopx(263)
				} else {
					return 310
				}
			},
			//获取可视区域高度
			getClineHeight() {
				var that = this;
				const res = uni.getSystemInfo({
					success: (res => {
						that.scrollHeight = res.windowHeight - that.getscrollHeight();
					})
				})
			}
		}
	}
</script>

<style lang="scss" scoped src="./restaurant_order.scss"></style>