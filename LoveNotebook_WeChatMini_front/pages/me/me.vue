<template>
	<view>
		<MainHeader></MainHeader>

		<view class="content">

			<view class="top">
				<u-avatar class="avatar" :src="user.src" size="large"></u-avatar>
				<view class="nickname">{{user.nickname}}</view>
				<view class="id">
					<view>ID:{{user.id}}</view>
					<image src="../../static/img/copy.png" @click="copy()"></image>
				</view>
				<view class="days">
					恋恋笔记本已经陪伴您<font>{{days}}</font>天
				</view>
			</view>

			<view class="bottom">
				<view class="menu" v-for="(item,index) in menu" @click="gotoMeItemPage(index)">
					<img class="img1" :src="item.icon">
					<view class="setting">{{item.title}}</view>

					<view class="new" v-if="index == 1 && bossOrderNotRead">
						<image src="../../static/img/new.png"></image>
						<view>{{item.bossOrderNotReadNum}}</view>
					</view>

					<img class="img2" :src="item.right">
					<view class="line" v-if="index === menu.length-1 ? false : true"></view>
				</view>
			</view>
		</view>

		<MyTabBar></MyTabBar>
	</view>
</template>

<script>
	import MainHeader from "../../components/main_header.vue"
	import MyTabBar from "../../components/my_tabBar.vue"
	import storage from "../../utils/storage";

	export default {
		components: {
			MainHeader,
			MyTabBar
		},
		data() {
			return {
				user: {
					src: "/static/img/test/avatar/sun.jpg",
					nickname: "听橘子海.",
					id: 165088071686,
					sex: 0
				},
				days: 10,
				bossOrderNotRead: false,
				menu: [

					{
						icon: "/static/img/me_menu/edit_icon.png",
						title: "编辑资料",
						right: "/static/img/me_menu/arrow_right.png",
						url: "../me_editMaterial/me_editMaterial"
					},
					{
						icon: "/static/img/me_menu/order_icon.png",
						title: "我的订单",
						right: "/static/img/me_menu/arrow_right.png",
						bossOrderNotReadNum: 0,
						url: "../me_myOrder/me_myOrder"
					},
					{
						icon: "/static/img/me_menu/loveBalance_icon.png",
						title: "爱心余额",
						right: "/static/img/me_menu/arrow_right.png",
						url: "../me_heart/me_heart"
					},
					{
						icon: "/static/img/me_menu/diary_icon.png",
						title: "我的日记",
						right: "/static/img/me_menu/arrow_right.png"
					},
					{
						icon: "/static/img/me_menu/question_icon.png",
						title: "问题反馈",
						right: "/static/img/me_menu/arrow_right.png",
						url: "../me_problem/me_problem"
					},
					{
						icon: "/static/img/me_menu/logout_icon.png",
						title: "退出登录",
						right: "/static/img/me_menu/arrow_right.png"
					}
				]
			};
		},
		onLoad() {
			this.init();
		},
		onShow() {
			this.init();
		},
		methods: {
			init() {
				this.$http.httpGetByToken('/user/beforeLogin').then(res => {
					storage.set("userinfo", res.data.data);
				})
				var userinfo = storage.get("userinfo");
				this.user.id = userinfo.userId;
				this.user.nickname = userinfo.userNickName;
				this.user.src = userinfo.userAvatar;
				this.user.sex = userinfo.userSex;
				this.days = this.$utils.daysDistanceToToday(userinfo.userRegisterDate);
				this.getbossOrderNotReadNum();
			},
			getbossOrderNotReadNum() {
				var that = this;
				this.$http.httpGetByToken("/order/checkAndGetBossOrderNotReadNum").then(res => {
					console.log(res);
					if (res.data.data != 0) {
						that.bossOrderNotRead = true
						that.menu[1].bossOrderNotReadNum = res.data.data;
					}else{
						that.bossOrderNotRead = false;
					}
				})
			},
			gotoMeItemPage(index) {
				if(index === this.menu.length - 1){
					storage.remove('token');
					uni.reLaunch({
						url:'../index/index'
					})
					return;
				}
				uni.navigateTo({
					url: this.menu[index].url
				})
			},
			copy() {
				var id = this.user.id
				uni.setClipboardData({
					data: id.toString(),
					success: () => {
						uni.showToast({ //提示
							title: `复制成功`,
							icon: 'success'
						})
					}
				});
			}
		}
	}
</script>

<style lang="scss" scoped src="./me.scss"></style>