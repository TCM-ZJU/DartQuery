package commands
{
	import business.delegates.RecordSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.RecordSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	
	import model.TsfModelLocator;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	
	import view.DisplayInfoView;
	import view.LCYJDisplayInfoView;
	import view.LinchuangHerbDosageView;
	import view.LinchuangWenxianView;
	import view.TreatInfoDisplayView;
	
	public class RecordSearchCommand implements ICommand, IResponder
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
			var recordSearchEvent:RecordSearchEvent = event as RecordSearchEvent;
			key = recordSearchEvent.key;
			source = recordSearchEvent.source;
			related = recordSearchEvent.related;
			pageIndex = recordSearchEvent.pageIndex;
			pageSize = recordSearchEvent.pageSize;
			searchType = modelLocator.searchType;
			var delegate:RecordSearchDelegate = new RecordSearchDelegate(this);
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
					if (displayView is LCYJDisplayInfoView) {
						var lcyjDisplayInfoView:LCYJDisplayInfoView = displayView as LCYJDisplayInfoView;
						lcyjDisplayInfoView.showRecord(data.result as XML);
					}
					else if (displayView is TreatInfoDisplayView) {
						var treatInfoDisplayView:TreatInfoDisplayView = displayView as TreatInfoDisplayView;
						treatInfoDisplayView.showRecord(data.result as XML);
					}
					else if (displayView is DisplayInfoView) {
						var displayInfoView:DisplayInfoView = displayView as DisplayInfoView;
						displayInfoView.init(data.result as XML);
					}
					else if (displayView is LinchuangWenxianView) {
						var wenxianView:LinchuangWenxianView = displayView as LinchuangWenxianView;
						wenxianView.init(data.result as XML);
					}
					else if (displayView is LinchuangHerbDosageView) {
						var herbDosageView:LinchuangHerbDosageView = displayView as LinchuangHerbDosageView;
						herbDosageView.init(data.result as XML);
					}
				//	var displayView:LCYJDisplayInfoView = tabNavigate.getChildAt(tabNavigate.selectedIndex) as LCYJDisplayInfoView;
				//	displayView.showRecord(data.result as XML);			
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function RecordSearchCommand()
		{
		}

	}
	
}