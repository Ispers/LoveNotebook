<template>
	<view>
		<MainHeader title="情侣广场"></MainHeader>
		<view class="content">
			<!-- 正文内容 -->
			<view class="rotationChart">
				<u-swiper :list="rotationChartList" height="350"></u-swiper>
			</view>

			<!-- 选项卡吸顶 -->
			<u-sticky :offset-top="stickyHeight">
				<view>
					<u-tabs :list="tabslist" 
					:is-scroll="false" 
					:current="tabscurrent"
					@change="tabchange"
					active-color="#ffc0cb"
					inactive-color="#666"
					bar-width="75"></u-tabs>
				</view>
			</u-sticky>

			<!-- 内容视图 -->
			<view v-if="tabscurrent == 0">
				
				<view class="item" v-for="(item,index) in neworders">
					<view @click="gotoOrderDetails(item.orderId)">
						<view class="name">这是{{item.restaurantName}}</view>
						<view class="lovetitle">{{item.orderTitle}}</view>
						<view class="foodimgs">
							<image :src="items.orderDetailsFoodImg" v-if="indexs < 3" v-for="(items, indexs) in item.orderDetails"></image>
						</view>
					</view>
					<view class="bottom">
						<image :src="item.userInfo.userAvatar"></image>
						<view class="mid">
							<view class="nickname">{{item.userInfo.userNickName}}</view>
							<view class="time">{{item.orderCreateTime}}</view>
						</view>
						<view class="right">
							<u-icon @click="timeCancelLike(index)"
							v-if="item.userIsLike == 1"
							 name="thumb-up-fill" 
							 color="#ffc0cb" 
							 size="35"></u-icon>
							 
							<u-icon @click="timeLike(index)"
							v-if="item.userIsLike == 0" 
							name="thumb-up" 
							color="#666" 
							size="35"></u-icon>
							
							<view>{{item.likeCount == null ? 0 : item.likeCount}}</view>
						</view>
					</view>
				</view>
				
				<view style="background-color: white; padding-top: 15rpx; padding-bottom: 15rpx;">
					<u-loadmore :status="status0" 
					:load-text="loadText" 
					color="#fc5c7d"/>
				</view>
			</view>
			
			<view v-if="tabscurrent == 1">
				
				<view class="item" v-for="(item,index) in hotorders">
					<view @click="gotoOrderDetails(item.orderId)">
						<view class="name">这是{{item.restaurantName}}</view>
						<view class="lovetitle">{{item.orderTitle}}</view>
						<view class="foodimgs">
							<image :src="items.orderDetailsFoodImg" v-if="indexs < 3" v-for="(items, indexs) in item.orderDetails"></image>
						</view>
					</view>
					<view class="bottom">
						<image :src="item.userInfo.userAvatar"></image>
						<view class="mid">
							<view class="nickname">{{item.userInfo.userNickName}}</view>
							<view class="time">{{item.orderCreateTime}}</view>
						</view>
						<view class="right">
							<u-icon @click="hotCancelLike(index)"
							v-if="item.userIsLike == 1"
							 name="thumb-up-fill" 
							 color="#ffc0cb" 
							 size="35"></u-icon>
							 
							<u-icon @click="hotLike(index)"
							v-if="item.userIsLike == 0" 
							name="thumb-up" 
							color="#666" 
							size="35"></u-icon>
							
							<view>{{item.likeCount == null ? 0 : item.likeCount}}</view>
						</view>
					</view>
				</view>
				
				<view style="background-color: white; padding-top: 15rpx; padding-bottom: 15rpx;">
					<u-loadmore :status="status1" 
					:load-text="loadText" 
					color="#fc5c7d"/>
				</view>
			</view>
		</view>
		
		<u-back-top 
		:scroll-top="scrollTop" 
		top="600"
		:icon-style="iconStyle"
		tips="顶部"
		:custom-style="customStyle"></u-back-top>
		
		<MyTabBar></MyTabBar>
	</view>
</template>

<script>
	import MainHeader from "../../components/main_header.vue"
	import MyTabBar from "../../components/my_tabBar.vue"
	import common from "../../utils/common";

	export default {
		components: {
			MainHeader,
			MyTabBar
		},
		data() {
			return {
				rotationChartList:[
					'http://101.42.15.201/images/1.jpg',
					'http://101.42.15.201/images/3.jpg',
					'http://101.42.15.201/images/2.jpg'
				],
				tabslist: [{
					name: '最新分享'
				}, {
					name: '热门推荐'
				}],
				tabscurrent:0,
				stickyHeight:180,
				status0: 'loadmore',
				status1: 'loadmore',
				loadText: {
					loadmore: '上拉加载更多',
					loading: '正在加载，喝杯茶吧...',
					nomore: '~实在没有啦~'
				},
				neworders:[],
				hotorders:[],
				newpage: 1,
				hotpage: 1,
				scrollTop: 0,
				iconStyle:{
					fontSize: '32rpx',
					color: '#fc5c7d'
				},
				customStyle:{
					backgroundColor: '#f1f1f1'
				}
			};
		},
		onLoad() {
			this.getstickyHeight();
		},
		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},
		onShow() {
			this.init();
		},
		onPullDownRefresh() {
			this.newpage = 1;
			this.hotpage = 1;
			this.neworders = [];
			this.hotorders = [];
			this.init();
			//刷新数据之后停止刷新效果
			uni.stopPullDownRefresh()
		},
		onReachBottom() {
			var that = this;
			if(this.tabscurrent == 0){
				if(this.status0 == 'nomore'){return;}
				this.status0 = 'loading';
				this.newpage += 1;
				this.$http.httpGetByToken("/order/getAllOrderTimePage/" + this.newpage).then(res=>{
					console.log(res);
					if(res.data.data != null){
						that.neworders = that.neworders.concat(res.data.data);
						that.status0 = 'loadmore';
					}else{
						that.status0 = 'nomore';
					}
				})
			}else{
				if(this.status1 == 'nomore'){return;}
				this.status1 = 'loading';
				this.hotpage += 1;
				this.$http.httpGetByToken("/order/getAllOrderHotPage/" + this.hotpage).then(res=>{
					console.log(res);
					if(res.data.data != null){
						that.hotorders = that.hotorders.concat(res.data.data);
						that.status1 = 'loadmore';
					}else{
						that.status1 = 'nomore';
					}
				})
			}
		},
		methods:{
			init(){
				if(this.neworders.length != 0){return;} 
				this.getNewOrders();
				this.getHotOrders();
			},
			timeCancelLike(index){
				var that = this;
				var orderId = this.neworders[index].orderId;
				
				this.$http.httpPostByToken("/order/cancelLikeOrder/" + orderId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.neworders[index].userIsLike = 0;
						that.neworders[index].likeCount -= 1;
						that.hotorders.length = 0;
						that.hotpage = 1;
					}
				})
			},
			timeLike(index){
				var that = this;
				var orderId = this.neworders[index].orderId;
				
				this.$http.httpPostByToken("/order/likeOrder/" + orderId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.neworders[index].userIsLike = 1;
						that.neworders[index].likeCount += 1;
						that.hotorders.length = 0;
						that.hotpage = 1;
					}
				})
			},
			hotCancelLike(index){
				var that = this;
				var orderId = this.hotorders[index].orderId;
				
				this.$http.httpPostByToken("/order/cancelLikeOrder/" + orderId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.hotorders[index].userIsLike = 0;
						that.hotorders[index].likeCount -= 1;
						that.neworders.length = 0;
						that.newpage = 1;
					}
				})
			},
			hotLike(index){
				var that = this;
				var orderId = this.hotorders[index].orderId;
				
				this.$http.httpPostByToken("/order/likeOrder/" + orderId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.hotorders[index].userIsLike = 1;
						that.hotorders[index].likeCount += 1;
						that.neworders.length = 0;
						that.newpage = 1;
					}
				})
			},
			getNewOrders(){
				var that = this;
				this.$http.httpGetByToken("/order/getAllOrderTimePage/" + this.newpage).then(res=>{
					console.log(res);
					that.neworders = res.data.data;
				})
			},
			getHotOrders(){
				var that = this;
				this.$http.httpGetByToken("/order/getAllOrderHotPage/" + this.hotpage).then(res=>{
					console.log(res);
					that.hotorders = res.data.data;
				})
			},
			gotoOrderDetails(orderId){
				// console.log(orderId);
				uni.navigateTo({
					url:"../square_orderDetails/square_orderDetails?orderId=" + orderId
				})
			},
			tabchange(index) {
				this.tabscurrent = index;
				if(this.tabscurrent == 0){
					if(this.neworders.length != 0){return;}
					this.getNewOrders()
				}else{
					if(this.hotorders.length != 0){return;}
					this.getHotOrders()
				}
			},
			getstickyHeight(){
				const res = uni.getSystemInfoSync();
				this.stickyHeight = common.pxTorpx(res.statusBarHeight + 44)
			}
		}
	}
</script>

<style lang="scss" scoped src="./square.scss"></style>