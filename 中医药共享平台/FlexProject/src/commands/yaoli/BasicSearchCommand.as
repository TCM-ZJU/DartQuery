package commands.yaoli
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.rpc.IResponder;
	import events.yaoli.BasicSearchEvent;
	import business.delegates.yaoli.BasicSearchDelegate;
	import model.TsfModelLocator;
	import mx.controls.Alert;
	
	import flexlib.containers.SuperTabNavigator;
	import view.yaoli.BasicResultView;
	
	public class BasicSearchCommand implements ICommand, IResponder
	{
		[Bindable] 
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var cate:String;
		public var source:String;
		public var related:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var searchType:int;
		public var identity:int;
		
		public function execute(event:CairngormEvent):void
		{
			var basicSearchEvent:BasicSearchEvent = event as BasicSearchEvent;
			key = basicSearchEvent.key;
			cate = basicSearchEvent.cate;
			source = basicSearchEvent.source;
			related = basicSearchEvent.related;
			pageIndex = basicSearchEvent.pageIndex;
			pageSize = basicSearchEvent.pageSize;
			searchType = modelLocator.searchType;
			identity = basicSearchEvent.identity;
			var delegate:BasicSearchDelegate = new BasicSearchDelegate(this);
			delegate.search(key, cate, source, related, pageIndex, pageSize, searchType, identity);
		}
		
		public function result(data:Object):void
		{
			//Alert.show(data.toString());
			if (data.result != null)
			{
				if (data.result is XML)
				{				

					var tabNavigate:SuperTabNavigator = modelLocator.resultView.tabNav;
					var displayView:Object = tabNavigate.getChildAt(tabNavigate.selectedIndex);
					if (displayView is BasicResultView)
					{
						var displayInfoView:BasicResultView = displayView as BasicResultView;
						displayInfoView.init(data.result as XML);
					}	
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}		
		
		public function BasicSearchCommand()
		{
			
		}

	}
}