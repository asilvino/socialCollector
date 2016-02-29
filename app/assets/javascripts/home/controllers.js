/**
 * Home controllers.
 */
define([], function() {
	'use strict';

	/** Controls the index page */
	var HomeCtrl = function($scope, $rootScope, $location, helper,UserSearch,UserCount,Pages) {
		$rootScope.pageTitle = 'Welcome';
		$scope.api={};
		$scope.api['models.Utils$FacebookPages']='facebook';
		$scope.api['models.Utils$InstagramPages']='instagram';
		

		$scope.users=[];
		$scope.pages = [];
		$scope.query = {};
		$scope.direction = {};
		$scope.direction.asc='▲';
		$scope.direction.desc='▼';
		$scope.direction.none='▼▲';
		$scope.itensperpage = 10; 

		$scope.query.direction = $location.search().direction||'desc';
		$scope.query.order = $location.search().order||'likesCount';
		$scope.query.date = $location.search().date||'';
		$scope.query.page = parseInt($location.search().page)||1;
		$scope.query.pages = $location.search().pages;
		$scope.query.keyword = $location.search().keyword;
		$scope.query.name = $location.search().name;
		$scope.query.api = $location.search().api||'none';

		Pages.query().$promise.then(function(response,error,callBack){
				if($scope.query.pages){
					$scope.query.pages = response.filter(function(res){return $scope.query.pages.indexOf(res.id)>-1;});
				}else{
					$scope.query.pages=[];
				}
				$scope.pages = response;

			},function(reason){
				console.log(reason);
			});
		$scope.updateTable = function(){
			UserSearch.query($location.search()).$promise.then(function(response,error,callBack){
				$scope.users = response.users;
			},function(reason){
				console.log(reason);
			});
			UserCount.query($location.search()).$promise.then(function(response,error,callBack){
				$scope.total = response.total;
				var pageInt = parseInt($scope.query.page);
				$scope.totalPages= Math.ceil($scope.total/$scope.itensperpage);
				var untilPageInt = ($scope.totalPages>(pageInt+6))?pageInt+6:($scope.totalPages+1);
				$scope.pagesAvailables= _.range(pageInt,untilPageInt);
				$scope.de = pageInt*$scope.users.length-$scope.users.length+1;
				$scope.ate = pageInt*$scope.users.length;
			},function(reason){
				console.log(reason);
			});
		};
		
		$scope.selectPages = function(pages){
			var pagesIds = pages.map(function(page){return page.id;}).toString();
			$location.search('pages',pagesIds);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
		};
		
		$scope.selectDate = function(date){
			$location.search('date',date);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
			$scope.query.date = date;
		};

		$scope.selectApi = function(api){
			$location.search('api',api);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
			$scope.query.api = api;
		};


		$scope.selectOrder = function(order){
			if(order!==$scope.query.order)
				$scope.query.direction='desc';
			else{
				if($scope.query.direction==='asc')
					$scope.query.direction='desc';
				else
					$scope.query.direction='asc';
			}
			$scope.query.order = order;
			$location.search('order',$scope.query.order);
			$location.search('direction',$scope.query.direction);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
		};
        $scope.addkeyword = function(keyword){
            $scope.query.keyword = keyword;
            $location.search('keyword',$scope.query.keyword);
            $scope.query.page = 1;
            $location.search('page',$scope.query.page);
        };
        $scope.addName = function(name){
            $scope.query.name = name;
            $location.search('name',$scope.query.name);
            $scope.query.page = 1;
            $location.search('page',$scope.query.page);
        };
		$scope.changePage = function(page){
			$scope.query.page = page>0?page:1;
			$location.search('page',$scope.query.page);
		};

	};
	HomeCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper','UserSearch','UserCount','Pages'];
	/** Controls the index page */
	var PostCtrl = function($scope, $rootScope, $location, helper,UserSearch,Post,Pages) {
		$rootScope.pageTitle = 'Welcome';
		$scope.api={};
		$scope.api['models.Utils$FacebookPages']='facebook';
		$scope.api['models.Utils$InstagramPages']='instagram';
		$scope.posts=[];
		$scope.pages = [];
		$scope.query = {};
		$scope.direction = {};
		$scope.direction.asc='▲';
		$scope.direction.desc='▼';
		$scope.direction.none='▼▲';

		$scope.query.direction = $location.search().direction||'desc';
		$scope.query.order = $location.search().order||'likesCount';
		$scope.query.date = $location.search().date||'';
		$scope.query.page = parseInt($location.search().page)||1;
		$scope.query.pages = $location.search().pages;
		$scope.query.keyword = $location.search().keyword;

		Pages.query().$promise.then(function(response,error,callBack){
				if($scope.query.pages){
					$scope.query.pages = response.filter(function(res){return $scope.query.pages.indexOf(res.id)>-1;})[0]||{};
				}else{
					$scope.query.pages={};
				}
				$scope.pages = response;
			},function(reason){
				console.log(reason);
			});
		$scope.updateTable = function(){
			Post.query($location.search()).$promise.then(function(response,error,callBack){
				$scope.posts = response.posts;
				$scope.total = response.total;
				var pageInt = parseInt($scope.query.page);
				$scope.totalPages= Math.ceil($scope.total/25);
				var untilPageInt = ($scope.totalPages>(pageInt+6))?pageInt+6:($scope.totalPages+1);
				$scope.pagesAvailables= _.range(pageInt,untilPageInt);
				$scope.de = pageInt*$scope.posts.length-$scope.posts.length+1;
				$scope.ate = pageInt*$scope.posts.length;
			},function(reason){
				console.log(reason);
			});
		};
		
		$scope.selectPages = function(page){
			var pagesIds = page.id;
			$scope.query.pages = page;
			$location.search('pages',pagesIds);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
		};
		
		$scope.selectDate = function(date){
			$location.search('date',date);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
			$scope.query.date = date;
		};

		$scope.selectOrder = function(order){
			if(order!==$scope.query.order)
				$scope.query.direction='desc';
			else{
				if($scope.query.direction==='asc')
					$scope.query.direction='desc';
				else
					$scope.query.direction='asc';
			}
			$scope.query.order = order;
			$location.search('order',$scope.query.order);
			$location.search('direction',$scope.query.direction);
			$scope.query.page = 1;
			$location.search('page',$scope.query.page);
		};
        $scope.addkeyword = function(keyword){
            $scope.query.keyword = keyword;
            $location.search('keyword',$scope.query.keyword);
            $scope.query.page = 1;
            $location.search('page',$scope.query.page);
        };
		$scope.changePage = function(page){
			$scope.query.page = page>0?page:1;
			$location.search('page',$scope.query.page);
		};

	};
	PostCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper','UserSearch','Post','Pages'];

	/** Controls the header */
	var HeaderCtrl = function($scope, userService, helper, $location) {
		// Wrap the current user from the service in a watch expression
		$scope.$watch(function() {
			var user = userService.getUser();
			return user;
		}, function(user) {
			$scope.user = user;
		}, true);

		$scope.logout = function() {
			userService.logout();
			$scope.user = undefined;
			$location.path('/');
		};
	};
	HeaderCtrl.$inject = ['$scope', 'userService', 'helper', '$location'];

	/** Controls the Single User page */
	var UserCtrl = function($scope, UserSearch, helper, $location,$routeParams) {
		// Wrap the current user from the service in a watch expression
		$scope.user = {};
		$scope.apiUrl={};
		$scope.apiUrl['models.Utils$FacebookPages']='http://facebook.com/';
		$scope.apiUrl['models.Utils$InstagramPages']='http://instagram.com/';
		$scope.api={};
		$scope.api['models.Utils$FacebookPages']='facebook';
		$scope.api['models.Utils$InstagramPages']='instagram';
		$scope.getUser = function() {
			if($routeParams.userId){
				UserSearch.get({id:$routeParams.userId}).$promise.then(function(response,error,callBack){
					$scope.user = response;
				},function(reason){
					console.log(reason);
				});
			}

		};
	};
	UserCtrl.$inject = ['$scope', 'UserSearch', 'helper', '$location','$routeParams'];

	/** Controls the Single User Words page */
	var UserWordsCtrl = function($scope, UserSearch, helper, $location,$routeParams,WordsUser) {
		// Wrap the current user from the service in a watch expression
		$scope.user = {};
		$scope.apiUrl={};
		$scope.apiUrl['models.Utils$FacebookPages']='http://facebook.com/';
		$scope.apiUrl['models.Utils$InstagramPages']='http://instagram.com/';
		$scope.api={};
		$scope.api['models.Utils$FacebookPages']='facebook';
		$scope.api['models.Utils$InstagramPages']='instagram';
		$scope.query = {};
		$scope.query.date = $location.search().date||'';
		$scope.selectDate = function(date){
			$location.search('date',date);
			$scope.query.date = date;
		};
		$scope.updateTable = function(){
			$scope.query.id = $routeParams.userId;
			WordsUser.get($scope.query).$promise.then(function(response,error,callBack){
				$scope.user = response.user;
				$scope.wordsPosts = response.posts;
				$scope.wordsComments = response.comments;
				$scope.totalPosts = Object.keys($scope.wordsPosts).length;
				$scope.totalComments = Object.keys($scope.wordsComments).length;
			},function(reason){
				console.log(reason);
			});
		};
	};
	UserWordsCtrl.$inject = ['$scope', 'UserSearch', 'helper', '$location','$routeParams','WordsUser'];

	/** Controls the FB/INSTA - Pages Words page */
	var PagesWordsCtrl = function($scope, UserSearch, helper, $location,$routeParams,WordsPages,Pages) {
		// Wrap the current user from the service in a watch expression
		$scope.user = {};
		$scope.apiUrl={};
		$scope.apiUrl['models.Utils$FacebookPages']='http://facebook.com/';
		$scope.apiUrl['models.Utils$InstagramPages']='http://instagram.com/';
		$scope.api={};
		$scope.api['models.Utils$FacebookPages']='facebook';
		$scope.api['models.Utils$InstagramPages']='instagram';
		$scope.query = {};
		$scope.query.date = $location.search().date||'';
		$scope.query.pages = $location.search().pages;
		$scope.selectDate = function(date){
			$location.search('date',date);
			$scope.query.date = date;
		};
		$scope.selectPages = function(pages){
			var pagesIds = pages.map(function(page){return page.id;}).toString();
			$location.search('pages',pagesIds);
		};
		Pages.query().$promise.then(function(response,error,callBack){
			if($scope.query.pages){
				$scope.query.pages = response.filter(function(res){return $scope.query.pages.indexOf(res.id)>-1;});
			}else{
				$scope.query.pages=[];
			}
			$scope.pages = response;
		},function(reason){
			console.log(reason);
		});
		
		$scope.updateTable = function(){
			WordsPages.query($location.search()).$promise.then(function(response,error,callBack){
				$scope.wordsPosts = response.posts;
				$scope.wordsComments = response.comments;
				$scope.totalPosts = Object.keys($scope.wordsPosts).length;
				$scope.totalComments = Object.keys($scope.wordsComments).length;
			},function(reason){
				console.log(reason);
			});
		};
	};
	PagesWordsCtrl.$inject = ['$scope', 'UserSearch', 'helper', '$location','$routeParams','WordsPages','Pages'];

	/** Controls the footer */
	var FooterCtrl = function(/*$scope*/) {
	};
	//FooterCtrl.$inject = ['$scope'];

	return {
		HeaderCtrl: HeaderCtrl,
		FooterCtrl: FooterCtrl,
		HomeCtrl: HomeCtrl,
		UserCtrl:UserCtrl,
		PostCtrl:PostCtrl,
		UserWordsCtrl:UserWordsCtrl,
		PagesWordsCtrl:PagesWordsCtrl
	};

});
