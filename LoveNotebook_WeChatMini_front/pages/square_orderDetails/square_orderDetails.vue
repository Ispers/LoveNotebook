<template>
	<view>
		<BackHeader title="订单详情"></BackHeader>
		
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
		
		<view class="main">
			
			<view class="main_item" v-for="(items,indexs) in order.orderDetails">
				<image :src="items.orderDetailsFoodImg"></image>
			
				<view class="main_item_name">
					<view class="name">{{items.orderDetailsFoodName}}</view>
					<view class="num">x&nbsp;{{items.orderDetailsFoodCount}}</view>
				</view>
			
				<view class="main_item_price">
					<image src="../../static/img/heart.png"></image>
					<view>{{items.orderDetailsFoodHeartCount}}</view>
				</view>
			</view>
			
			<view class="love_word">
				<view class="title">一句情话</view>
				<view class="text">{{order.orderTitle}}</view>
			</view>
			
			<view class="use_heart">
				<view class="text">本单消耗爱心</view>
				<image src="../../static/img/heart.png"></image>
				<view class="num">{{order.orderTotalFoodHeartCount}}</view>
			</view>
			
			<view class="time">
				<view>备注:&nbsp;&nbsp;{{order.orderNote}}</view>
			</view>
			
			<view class="time">
				<view>下单时间:&nbsp;&nbsp;{{order.orderCreateTime}}</view>
			</view>
			
			<view class="commentcount">
				<view>共{{orderComments.orderCommentCount}}条评论记录</view>
			</view>
			
			<view class="comment" v-for="(item,index) in orderComments.orderComments">
				<image :src="item.orderCommentUserInfo.userAvatar"></image>
				<view class="mid">
					<view class="mid-top">
						<view class="name">{{item.orderCommentUserInfo.userNickName}}</view>
						<view class="times">{{item.orderCommentCreateTime}}</view>
					</view>
					<view class="mid-main">
						{{item.orderCommentContent}}
					</view>
				</view>
				<view class="mid-right">
					<u-icon name="thumb-up"
					@click="like(index)"
					v-if="item.userIsLike == 0" 
					color="#666" size="35"></u-icon>
					 
					<u-icon name="thumb-up-fill"
					@click="cancelLike(index)"
					v-if="item.userIsLike == 1" 
					color="#ffc0cb" size="35"></u-icon>
					<view>{{item.likeCount == null ? 0 : item.likeCount}}</view>
				</view>
			</view>
			
			
			<!-- ~没有更多啦~ -->
			<!-- <view class="nothing" :style="'height:' + nothingHeight + 'px'">~没有更多啦~</view> -->
			<view :style="'height:' + nothingHeight + 'px'" 
			style="background-color: white; padding-top: 20rpx;">
				<u-loadmore :status="status" 
				:load-text="loadText" 
				color="#fc5c7d"/>
			</view>
			
		</view>
		
		<!-- 机型底部适配 -->
		
		<view class="bottom" :style="'bottom:' + bottom + 'px'" style="z-index: 9999;">
			<input type="text"
			 v-model = "commentText"
			:adjust-position="false"
			 placeholder="期待您的评论" 
			 confirm-type = "done"
			 @focus="getBottom()"
			 @blur="getBottom1()">
			<view @click="sendComment()">发送</view>
		</view>
		
		<u-back-top
		:scroll-top="scrollTop" 
		top="400"
		:icon-style="iconStyle"
		tips="顶部"
		:custom-style="customStyle"></u-back-top>
		
		<view v-if="iosbottom"
		style="width: 100%;height: 34px; background-color: #f6f6f6; position: fixed; bottom: 0; z-index: 9999;"></view>
		
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
				orderId:1,
				userInfo: {},
				restaurantInfo: {},
				bottom:0,
				iosbottom: false,
				nothingHeight:0,
				order:{},
				page:1,
				orderComments:{},
				commentText:"",
				status: 'loadmore',
				loadText: {
					loadmore: '上拉加载更多',
					loading: '正在加载，喝杯茶吧...',
					nomore: '~实在没有啦~'
				},
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
		onLoad(option) {
			this.orderId = option.orderId;
			// console.log(this.orderId); 
			this.init(option.orderId);
			this.checkIosBottom();
		},
		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},
		onUnload(){
			uni.offKeyboardHeightChange();
		},
		onReachBottom() {
			var that = this;
			if(this.status == 'nomore'){return;}
			
			this.status = 'loading';
			this.page += 1;
			this.$http.httpGetByToken("/order/getOrderCommentInfo/" + this.orderId + "/" + this.page).then(res=>{
				console.log(res);
				if(res.data.data.orderComments.length != 0){
					that.orderComments.orderComments = that.orderComments.orderComments.concat(res.data.data.orderComments);
					that.status = 'loadmore';
				}else{
					that.status = 'nomore';
				}
			})
		},
		methods:{
			init(orderId){
				this.getorder(orderId);
				this.getOrderComments(orderId);
				this.getUserInfoAndRestaurantInfo();
			},
			getorder(orderId){
				var that = this;
				this.$http.httpGetByToken("/order/getOrderAndOrderDetailsByOrderId/" + orderId).then(res=>{
					console.log(res);
					that.order = res.data.data;
				})
			},
			getOrderComments(orderId){
				var that = this;
				this.$http.httpGetByToken("/order/getOrderCommentInfo/" + orderId + "/" + this.page).then(res=>{
					console.log(res);
					that.orderComments = res.data.data;
				})
			},
			getUserInfoAndRestaurantInfo(){
				var that = this;
				this.$http.httpGetByToken("/order/getUserInfoAndRestaurantInfoByOrderId/" + this.orderId).then(res=>{
					console.log(res);
					that.userInfo = res.data.data.user
					that.restaurantInfo = res.data.data.restaurant
				})
			},
			sendComment(){
				var that = this;
				var orderComment = {}
				orderComment.orderId = this.orderId;
				orderComment.orderCommentContent = this.commentText;
				this.$http.httpPostByToken("/order/addOrderCommentInfo", orderComment).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.orderComments.orderComments = [];
						that.page = 1;
						that.getOrderComments(that.orderId);
					}
				})
			},
			cancelLike(index){
				var that = this;
				this.$http.httpPostByToken("/order/cancelLikeOrderComment/" + this.orderComments.orderComments[index].orderCommentId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.orderComments.orderComments[index].userIsLike = 0;
						that.orderComments.orderComments[index].likeCount -= 1;
					}
				})
			},
			like(index){
				var that = this;
				this.$http.httpPostByToken("/order/likeOrderComment/" + this.orderComments.orderComments[index].orderCommentId 
				+ "/" + this.orderId).then(res=>{
					console.log(res);
					if(res.data.flag == true){
						that.orderComments.orderComments[index].userIsLike = 1;
						that.orderComments.orderComments[index].likeCount += 1;
					}
				})
			},
			getBottom(){
				var that = this;
				uni.onKeyboardHeightChange(res => {
					//这里正常来讲代码直接写
					that.bottom = res.height
				});
			},
			getBottom1(){
				this.checkIosBottom();
			},
			checkIosBottom() {
				const res = uni.getSystemInfo()
				if (res.platform === 'ios') {
					console.log("ios")
					this.iosbottom = true;
					this.nothingHeight = 120
					this.bottom = 34;
				} else if (res.platform === 'android') {
					console.log("android")
					this.iosbottom = false;
					this.nothingHeight = 120-34
					this.bottom = 0;
				} else {
					this.iosbottom = true;
					this.nothingHeight = 120
					this.bottom = 34
				}
			}
		}
	}
</script>

<style lang="scss" scoped src="../me_myOrderDetails/me_myOrderDetails.scss"></style>