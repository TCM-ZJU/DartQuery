package commands.yaoli
{
	import business.delegates.yaoli.StatSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.yaoli.StatSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	
	import model.TsfModelLocator;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	
	import view.yaoli.GeneralInfoView;
	import view.yaoli.StatInfoView;
	
	public class StatSearchCommand implements ICommand, IResponder
	{
		
		[Bindable] 
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var cate:String;
		public var source:String;
		public var related:String;
		public var field:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var searchType:int;
		public var identity:int;

		public function execute(event:CairngormEvent):void
		{
			var statSearchEvent:StatSearchEvent = event as StatSearchEvent;
			key = statSearchEvent.key;
			cate = statSearchEvent.cate;
			source = statSearchEvent.source;
			related = statSearchEvent.related;
			field = statSearchEvent.field;
			pageIndex = statSearchEvent.pageIndex;
			pageSize = statSearchEvent.pageSize;
			searchType = modelLocator.searchType;
			identity = statSearchEvent.identity;
			var delegate:StatSearchDelegate = new StatSearchDelegate(this);
			delegate.search(key, cate, source, related, field, pageIndex, pageSize, searchType, identity);
		}

		public function result(data:Object):void
		{
			//Alert.show(data.toString());
			if (data.result != null)
			{
				if (data.result is XML)
				{			
				//	Alert.show(data.result);	
					var tabNavigate:SuperTabNavigator = modelLocator.resultView.tabNav;
					var displayView:Object = tabNavigate.getChildAt(tabNavigate.selectedIndex);
					if (displayView is StatInfoView)
					{
						var displayInfoView:StatInfoView = displayView as StatInfoView;
						displayInfoView.init(data.result as XML);
					}
					else if (displayView is GeneralInfoView)
					{
						var generalInfoView:GeneralInfoView = displayView as GeneralInfoView;
						generalInfoView.showSimpleInfo(data.result as XML);
					}	
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}		
						
		public function StatSearchCommand()
		{
		}

	}
}