package commands
{
	import business.delegates.RelationSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.RelationSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	
	import model.TsfModelLocator;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	
	import view.DisplayInfoView;
	import view.LinchuangGeneralInfoView;
	import view.TreatInfoDisplayView;
	
	
	public class RelationSearchCommand implements ICommand, IResponder
	{
		
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var source:String;
		public var related:String;
		public var pageIndex:int;
		public var pageSize:int;
		public var searchType:int;
		
		public function execute(event:CairngormEvent):void
		{
			var relationSearchEvent:RelationSearchEvent = event as RelationSearchEvent;
			key = relationSearchEvent.key;
			source = relationSearchEvent.source;
			related = relationSearchEvent.related;
			pageIndex = relationSearchEvent.pageIndex;
			pageSize = relationSearchEvent.pageSize;
			searchType = modelLocator.searchType;
			var delegate:RelationSearchDelegate = new RelationSearchDelegate(this);
			delegate.search(key, source, related, pageIndex, pageSize, searchType);
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
					if (displayView is DisplayInfoView)
					{
						var displayInfoView:DisplayInfoView = displayView as DisplayInfoView;
						displayInfoView.init(data.result as XML);
					}
					else if (displayView is TreatInfoDisplayView)
					{
						var treatInfoDisplayView:TreatInfoDisplayView = displayView as TreatInfoDisplayView;
						treatInfoDisplayView.showRecord(data.result as XML);
					}
					else if (displayView is LinchuangGeneralInfoView)
					{
						var linchuangGenernalView:LinchuangGeneralInfoView = displayView as LinchuangGeneralInfoView;
						linchuangGenernalView.showSimpleInfo(data.result as XML);
					}
				//	var displayView:DisplayInfoView = tabNavigate.getChildAt(tabNavigate.selectedIndex) as DisplayInfoView;
				//	displayView.init(data.result as XML);			
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function RelationSearchCommand()
		{
		}

	}
}