<template>
	<view>
		<u-tabbar v-model="current" :list="list" :mid-button="false" active-color="#ffc0cb"
			inactive-color="#666"></u-tabbar>
	</view>
</template>

<script>
	export default {
		name: "my_tabBar",
		data() {
			return {
				list: [{
						iconPath: "/static/img/tarbar/home.png",
						selectedIconPath: "/static/img/tarbar/home_1.png",
						text: '首页',
						customIcon: false,
						pagePath: '/pages/index/index'
					},
					{
						iconPath: "/static/img/tarbar/restaurant.png",
						selectedIconPath: "/static/img/tarbar/restaurant_1.png",
						text: '恋爱餐厅',
						customIcon: false,
						pagePath: '/pages/restaurant/restaurant'
					},
					{
						iconPath: "/static/img/tarbar/square.png",
						selectedIconPath: "/static/img/tarbar/square_1.png",
						text: '情侣广场',
						customIcon: false,
						pagePath: '/pages/square/square'
					},
					{
						iconPath: "/static/img/tarbar/me.png",
						selectedIconPath: "/static/img/tarbar/me_1.png",
						text: '我',
						customIcon: false,
						pagePath: '/pages/me/me'
					},
				],
				current: 0

			};
		}
	}
</script>

<style lang="scss">

</style>