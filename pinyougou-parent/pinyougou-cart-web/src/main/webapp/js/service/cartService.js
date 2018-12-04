app.service('cartService',function($http){
//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartListByCookie.do');
	}


	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addCartList.do?itemId='+itemId+'&num='+num);
	}
	
	
	
	this.sum= function(cartList){
		var totalValue={totalNum:0,totalFee:0}
		for(var i = 0;i<cartList.length;i++){
			var cart = cartList[i];
			for(var j = 0;j<cart.orderItemList.length;j++){
                totalValue.totalNum	 +=cart.orderItemList[j].num;
                totalValue.totalFee +=cart.orderItemList[j].totalFee;
			}
		}
		return totalValue;
	}
    /**
     * 获取当前用户的地址列表
     */
	this.findAddressList=function(){
		return	$http.get("address/findListByUser.do");
	}

});