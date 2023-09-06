<template>
	<view class="main">
		<view class="left">
			<u-avatar :src="user.userAvatar" size="large"></u-avatar>
			<view>{{user.userNickName}}</view>
		</view>

		<view class="middle">
			已经在一起<font>{{day}}</font>天
		</view>

		<view class="right">
			<u-avatar :src="user.lover.userAvatar" size="large"></u-avatar>
			<view>{{user.lover.userNickName}}</view>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			user: {
				type: Object,
				default: function() {
					return {
						userId: 0,
						userSex: 0,
						userRegisterDate: "",
						userIsLove: 0,
						userLoveDate: "",
						userNickName: "",
						userAvatar: "",
						userLoveId: 0,
						lover: {
							userId: 0,
							userSex: 0,
							userRegisterDate: "",
							userIsLove: 0,
							userLoveDate: "",
							userNickName: "",
							userAvatar: "",
							userLoveId: 0,
						}
					}
				}
			}
		},
		data() {
			return {
				day: this.$utils.daysDistanceToToday(this.user.userLoveDate)
			};
		},
		methods: {

		}
	}
</script>

<style scoped lang="scss" src="./scss/index_top_islove.scss">

</style>