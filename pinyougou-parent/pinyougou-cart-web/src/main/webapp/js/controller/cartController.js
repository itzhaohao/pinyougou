app.controller('cartController',function($scope,cartService){
//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
		function(response){
		$scope.cartList=response;
         // sum();
		$scope.totalValue = cartService.sum($scope.cartList)
			}
		);
	}
	
	$scope.addNum=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
				function(response){
					if(response.success){
						$scope.findCartList();
						
					}else{
						alert("新增失败");
					}
				})
	}


    /**
     * 查询收件人信息
     */321
	$scope.findAddressList=function(){
        cartService.findAddressList().success(
        	function(response){
        		$scope.addressList = response;
			}
		);
	}

    /**
     * 用户用户选择地址
     * @param address
     */
	$scope.selectedAddress=function(address){
	    $scope.address=address;
    }

    /**
     * 用户用户选择地址
     * @param address
     * @returns {boolean}
     */
    $scope.isSelected=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.order={'paymenType':'1'};
    $scope.selectPayType=function(type){
        $scope.order.paymenType=type;
	}
	
	/*sum= function(){
		$scope.totalNum=0;
		$scope.totalFee=0;
		for(var i = 0;i<$scope.cartList.length;i++){
			var cart = $scope.cartList[i];
			for(var j = 0;j<cart.orderItemList.length;j++){
				$scope.totalNum +=cart.orderItemList[j].num;
				$scope.totalFee +=cart.orderItemList[j].totalFee;
			}
		}
	}*/





});