<template>
	<view>
		<BackHeader title="爱心余额"></BackHeader>
		<view class="box">
			<view class="top">
				<view class="text">您当前的爱心余额为：<span>{{userinfo.userHeartCount}}</span></view>
				<image src="../../static/img/heart.png"></image>
			</view>
			<view class="button" @click="addHeart()">点我随机增加爱心数量</view>
		</view>
	</view>
</template>

<script>
import storage from '../../utils/storage';
import BackHeader from "../../components/back_header.vue"

	export default {
		data() {
			return {
				userinfo:{}
			};
		},
		components:{
			BackHeader
		},
		onLoad() {
			this.init();
		},
		methods:{
			init(){
				this.userinfo = storage.get("userinfo");
			},
			async addHeart(){
				// 前端不做随机,后端做,后期可能会修改获取爱心方式
				var res = await this.$http.httpPostByToken("/user/userAddHeart");
				console.log(res);
				if(res.data.flag){
					storage.setToken(res.data.data.token)
					uni.showToast({
						icon:"success",
						title:"添加成功",
						complete : () => {
							setTimeout(
							()=>{
								uni.switchTab({
									url:"../me/me"
								})
							},1500)
						}
					})
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.box{
		background-color: #fff;
		padding-top: 50rpx;
		padding-bottom: 50rpx;
		.top{
			display: flex;
			margin-left: 10%;
			margin-bottom: 100rpx;
		}
		.text{
			font-size: 40rpx;
			span{
				font-weight: bold;
				color: hotpink;
			}
		}
		image{
			width: 40rpx;
			height: 40rpx;
			transform: translateY(8rpx);
			margin-left: 20rpx;
		}
		.button{
			background-color: $maincolor;
			color: #fff;
			width: 80%;
			height: 80rpx;
			margin: auto;
			border-radius: 20rpx;
			font-size: 35rpx;
			text-align: center;
			line-height: 80rpx;
			
		}
	}
</style>