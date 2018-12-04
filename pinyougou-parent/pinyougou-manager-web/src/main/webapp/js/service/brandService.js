app.service("brandService",function($http){
		<!--分页查询所有--> 
		this.findPage=function(page, rows){
			return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
		}
		this.search=function(page, rows,searchEntity){
			return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity);
		}

		<!--保存方法--> 
		this.add=function(entity){
		return $http.post('../brand/add.do',entity);
			
		}
		this.update=function(entity){
			return $http.post('../brand/update.do',entity);
		}
		<!--查询一个-->
		this.findOne=function(id){
			return $http.get('../brand/findOne.do?id='+id);
		}
		
		<!--删除-->
		this.dele=function(selectIds){
			return $http.get('../brand/delete.do?ids='+selectIds);
		}
		this.selectOptionList=function(){
			return $http.get('../brand/selectOptionList.do');
		}
	})