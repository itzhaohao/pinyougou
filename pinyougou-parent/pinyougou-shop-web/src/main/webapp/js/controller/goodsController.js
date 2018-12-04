 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.entity={tbGoods:{},tbGoodsDesc:{introduction:{},itemImages:[],specificationItems:[]},items:[]};
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
				function(response){
					$scope.entity= response;
//					alert($scope.entity.tbGoodsDesc.customAttributeItems);
					if($scope.entity.tbGoodsDesc.itemImages.length!=0){
						$scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages);
					}
					$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
					$scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
					for(var i =0;i<$scope.entity.items.length;i++){
						$scope.entity.items[i].spec=JSON.parse($scope.entity.items[i].spec);
					}
					editor.html($scope.entity.tbGoodsDesc.introduction);
				}
		);				
	}
	
	
	
	
	//保存 
	$scope.save=function(){		
		//提取文本编辑器的值
		alert(editor.html());
		if(editor.html()!=null && !""==editor.html()){
			$scope.entity.tbGoodsDesc.introduction=editor.html();
		}
		var serviceObject;//服务层对象
		if($scope.entity.tbGoods.id==null){
			serviceObject=goodsService.add( $scope.entity );
		}else{
			serviceObject=	goodsService.update($scope.entity)
		}
		serviceObject.success(
				function(response){
					if(response.success){
						alert("添加成功");
						//重新查询 
			        	$location.href="goods.html";
					}else{
						alert(response.message);
					}
				}		
			);				
	}
	
	
	$scope.add=function(){	
		$scope.entity.tbGoodsDesc.introduction=editor.html();
		goodsService.add( $scope.entity ).success(
			function(response){
				if(response.success){
					alert("添加成功");
					//重新查询 
		        	$scope.entity={};//重新加载
		        	editor.html('');
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
					
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//文件上传
	$scope.image_entity={url:{}};
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
				function(response){
					alert(response.message);
					if(response.success){
						$scope.image_entity.url=response.message;
					}else{
						alert(response.message);
					}
					
				}).error(
						function(){
							alert("上传发生错误");
						})
	}
	
	
	$scope.entity.tbGoodsDesc.itemImages=[];
	$scope.add_image_entity=function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}
	
	
	
	$scope.remove_image_entity=function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
	}
	
	
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(
		function(response){
		$scope.itemCat1List=response;
		});
		
		
	}
	
	//读取二级分类
	$scope.$watch('entity.tbGoods.category1Id',function(newValue, oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
				$scope.itemCat2List=response;
				$scope.itemCat3List=[];
				});
	})
	
	//读取三级分类
	
		$scope.$watch('entity.tbGoods.category2Id',function(newValue, oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
				$scope.itemCat3List=response;
				});
	})
	
	//读取模板值
	
	$scope.$watch('entity.tbGoods.category3Id',function(newValue, oldValue){
		itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.tbGoods.typeTemplateId=response.typeId;
				});
		

	});

	
	//读取品牌值
	$scope.entity.specificationItems=[];
	$scope.$watch('entity.tbGoods.typeTemplateId',function(newValue, oldValue){
		typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.tbTypeTemplate=response;
//					alert('模板品牌部分'+$scope.tbTypeTemplate.brandIds);
					$scope.tbTypeTemplate.brandIds=JSON.parse($scope.tbTypeTemplate.brandIds);
					if($location.search()['id']==null){
					$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.tbTypeTemplate.customAttributeItems);
					}
				});
		//查询模板中规格名称部分和规格选项部分合一,
		typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.entity.specificationItems=response;
					
				});
		

	});
	
	//用户选择规格
	$scope.findSpecItems=function($event,name,value){
	var object = $scope.selectObjecByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",name);
		if(object!=null){
			if($event.target.checked==true){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value));
				if(object.attributeValue.length==0){
					$scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object));
				}
			}
		}else{
			
			$scope.entity.tbGoodsDesc.specificationItems.push({attributeName:name,attributeValue:[value]})
		}
	}
	
	//创建sku表
	$scope.createTable=function(){
	if($scope.entity.tbGoodsDesc.specificationItems.length==0){
		$scope.entity.items=[];
	}else{
		$scope.entity.items=[{spec:{},price:0,num:"9999",isDefault:"0",status:"0"}];
	}
		var items = $scope.entity.tbGoodsDesc.specificationItems
		for(i=0;i<items.length;i++){
			$scope.entity.items=$scope.addColum($scope.entity.items,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	
	$scope.addColum=function(list,columName,columValues){
		var newList = [];
		for(var i =0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<columValues.length;j++){
				var newRow = JSON.parse(JSON.stringify(oldRow))
				newRow.spec[columName]=columValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	$scope.status=['未审核','已审核','审核未通过','已关闭'];
	$scope.catName=[];
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
				function(response){
					for(var i=0;i<response.length;i++){
						$scope.catName[response[i].id]=response[i].name;
					}	
				
				});
	}
	//规格复选框状态判断
	$scope.checkAttributeValue=function(text,optionName){
		var object = $scope.selectObjecByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",text);
		if(object!=null){
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	
	
	
	/*$scope.selectItemCat2List=function(){
		itemCatService.findByParentId($scope.entity.goods.category1Id).success(
		function(response){
		$scope.itemCat2List=response;
		});
	}*/
	
/*	$scope.selectItemCat3List=function(){
		itemCatService.findByParentId($scope.entity.goods.category2Id).success(
		function(response){
		$scope.itemCat3List=response;
		});
	}*/
	
	
	


});	
