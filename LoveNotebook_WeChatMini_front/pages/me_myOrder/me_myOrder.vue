<template>
	<view>
		<BackHeader title="我的订单"></BackHeader>

		<view>
			<view>
				<u-tabs-swiper ref="uTabs" 
				:list="list" 
				:current="current" 
				@change="tabsChange" 
				:is-scroll="false"
				swiperWidth="750"
				active-color="#ffc0cb"
				inactive-color="#666"></u-tabs-swiper>
			</view>
			
			<swiper :current="swiperCurrent" 
			@transition="transition" 
			@animationfinish="animationfinish"
			:style="'height:' + scrollHeight + 'px;'">

				<swiper-item class="swiper-item">
					<scroll-view scroll-y style="height: 100%; width: 100%;" @scrolltolower="onreachBottom">
						
						<view class="scroll-item" v-for="(item, index) in orders">
							<view  @click="gotoMyOrderDetails(index)">
								<view class="top">
									<view class="title">{{item.orderTitle}}</view>
									<view class="orderStatus">已下单</view>
								</view>
								<view class="mid">
									<image :src="items.orderDetailsFoodImg" v-if="indexs < 3" v-for="(items, indexs) in item.orderDetails"></image>
									<view class="mid-right">
										<view class="mid-right-top">
											<view class="new" v-if="isboss && item.orderBossIsRead == 0"></view>
											<image src="../../static/img/heart.png"></image>
											<view>{{item.orderTotalFoodHeartCount}}</view>
										</view>
										<view class="mid-right-bottom">
											共&nbsp;{{item.orderTotalFoodTypeCount}}&nbsp;件
										</view>
									</view>
								</view>
							</view>
							<view class="bottom">
								<view @click="deleteOrder(index)">删除订单</view>
							</view>
						</view>
						
					</scroll-view>
				</swiper-item>

				<swiper-item class="swiper-item">
					<scroll-view scroll-y style="height: 100%; width: 100%;" @scrolltolower="onreachBottom">
						
						<view class="scroll-item" v-if="item.orderIsShare == 1" v-for="(item, index) in orders">
							<view @click="gotoMyOrderDetails(index)">
								<view class="top">
									<view class="title">{{item.orderTitle}}</view>
									<view class="orderStatus">已下单</view>
								</view>
								<view class="mid">
									<image :src="items.orderDetailsFoodImg" v-if="indexs < 3" v-for="(items, indexs) in item.orderDetails"></image>
									<view class="mid-right">
										<view class="mid-right-top">
											<view class="new" v-if="isboss && item.orderBossIsRead == 0"></view>
											<image src="../../static/img/heart.png"></image>
											<view>{{item.orderTotalFoodHeartCount}}</view>
										</view>
										<view class="mid-right-bottom">
											共&nbsp;{{item.orderTotalFoodTypeCount}}&nbsp;件
										</view>
									</view>
								</view>
							</view>
							<view class="bottom2">
								<view @click="cancelShareOrder(index)">取消分享</view>
								<view @click="deleteOrder(index)">删除订单</view>
							</view>
						</view>
						
					</scroll-view>
				</swiper-item>

				<swiper-item class="swiper-item">
					<scroll-view scroll-y style="height: 100%; width: 100%;" @scrolltolower="onreachBottom">
						
						<view class="scroll-item" v-if="item.orderIsShare == 0" v-for="(item, index) in orders">
							<view @click="gotoMyOrderDetails(index)">
								<view class="top">
									<view class="title">{{item.orderTitle}}</view>
									<view class="orderStatus">已下单</view>
								</view>
								<view class="mid">
									<image :src="items.orderDetailsFoodImg" v-if="indexs < 3" v-for="(items, indexs) in item.orderDetails"></image>
									<view class="mid-right">
										<view class="mid-right-top">
											<view class="new" v-if="isboss && item.orderBossIsRead == 0"></view>
											<image src="../../static/img/heart.png"></image>
											<view>{{item.orderTotalFoodHeartCount}}</view>
										</view>
										<view class="mid-right-bottom">
											共&nbsp;{{item.orderTotalFoodTypeCount}}&nbsp;件
										</view>
									</view>
								</view>
							</view>
							<view class="bottom2">
								<view @click="shareOrder(index)">分享订单</view>
								<view @click="deleteOrder(index)">删除订单</view>
							</view>
						</view>
						
					</scroll-view>
				</swiper-item>
			</swiper>
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
				list: [{
					name: '全部'
				}, {
					name: '已分享'
				}, {
					name: '未分享'
				}],
				// 因为内部的滑动机制限制，请将tabs组件和swiper组件的current用不同变量赋值
				current: 0, // tabs组件的current值，表示当前活动的tab选项
				swiperCurrent: 0, // swiper组件的current值，表示当前那个swiper-item是活动的
				scrollHeight: 0,
				orders:[],
				isboss:false
			};
		},
		onLoad() {
			this.getClineHeight();
			this.init();
		},
		onShow() {
			this.init();
		},
		methods: {
			init() {
				this.getOrderAndOrderDetailsByUserId();
				var userinfo = storage.get("userinfo");
				userinfo.userRestaurantLever == 0 ? (this.isboss = false) : (this.isboss = true)
			},
			getOrderAndOrderDetailsByUserId(){
				var that = this;
				this.$http.httpGetByToken("/order/getOrderAndOrderDetailsByUserId").then(res=>{
					console.log(res);
					that.orders = res.data.data;
				})
			},
			deleteOrder(index){
				var that = this;
				this.$http.httpPostByToken("/order/deleteOrderByOrderId/" + this.orders[index].orderId).then(res=>{
					console.log(res);
					that.init();
				})
			},
			// 取消分享
			cancelShareOrder(index){
				var that = this;
				this.$http.httpPostByToken("/order/cancelShareOrder/" + this.orders[index].orderId).then(res=>{
					console.log(res);
					that.init()
				})
			},
			// 分享
			shareOrder(index){
				var that = this;
				this.$http.httpPostByToken("/order/shareOrder/" + this.orders[index].orderId).then(res=>{
					console.log(res);
					that.init()
				})
			},
			gotoMyOrderDetails(index){
				uni.navigateTo({
					url:"../me_myOrderDetails/me_myOrderDetails?orderId="+ this.orders[index].orderId
				})
			},
			// tabs通知swiper切换
			tabsChange(index) {
				this.swiperCurrent = index;
			},
			// swiper-item左右移动，通知tabs的滑块跟随移动
			transition(e) {
				let dx = e.detail.dx;
				this.$refs.uTabs.setDx(dx);
			},
			// 由于swiper的内部机制问题，快速切换swiper不会触发dx的连续变化，需要在结束时重置状态
			// swiper滑动结束，分别设置tabs和swiper的状态
			animationfinish(e) {
				let current = e.detail.current;
				this.$refs.uTabs.setFinishCurrent(current);
				this.swiperCurrent = current;
				this.current = current;
			},
			// scroll-view到底部加载更多
			onreachBottom() {

			},
			getscrollHeight() {
				const res = uni.getSystemInfoSync()
				if (res.platform === 'ios') {
					return 44 + res.statusBarHeight + 41 + 34
				} else if (res.platform === 'android') {
					return 48 + res.statusBarHeight + 41
				} else {
					return 150
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

<style lang="scss" scoped src="./me_myOrder.scss"></style>