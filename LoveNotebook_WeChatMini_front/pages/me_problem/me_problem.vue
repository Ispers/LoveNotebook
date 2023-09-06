<template>
	<view>
		<BackHeader title="问题反馈"></BackHeader>
		<u-input class="text"
		 v-model="value" 
		:type="type" 
		:border="border" 
		:height="height" 
		:auto-height="autoHeight"
		 placeholder="请输入您遇到的问题,我们会及时修正..."
		 placeholder-style="font-size:30rpx"
		 style="background-color: red;"/>
		 
		 <view class="btn" @click="submit()">
		 	提交反馈
		 </view>
	</view>
</template>

<script>
	import storage from '../../utils/storage';
	import BackHeader from "../../components/back_header.vue"
	
	export default {
		components:{
			BackHeader
		},
		data() {
			return {
				value: '',
				type: 'textarea',
				border: true,
				height: 200,
				autoHeight: true
			};
		},
		onLoad() {
			
		},
		methods:{
			submit(){
				var value = this.value;
				console.log(value);
				
				// if(res.data.flag){
				// 	storage.setToken(res.data.data.token)
				// 	uni.showToast({
				// 		icon:"success",
				// 		title:"添加成功",
				// 		complete : () => {
				// 			setTimeout(
				// 			()=>{
				// 				uni.switchTab({
				// 					url:"../me/me"
				// 				})
				// 			},1500)
				// 		}
				// 	})
				// }
			}
		}
	}
</script>

<style lang="scss" scoped>
.text :nth-child(1){
	background-color: #fff;
}
.btn{
	background-color: $maincolor;
	color: #fff;
	width: 70%;
	height: 100rpx;
	border-radius: 20rpx;
	text-align: center;
	line-height: 100rpx;
	margin: auto;
	margin-top: 50rpx;
}
</style>
