package control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import commands.*;
	
	import events.*;
	
	import events.yaoli.*;
	import commands.yaoli.*;
	public class TsfController extends FrontController
	{
		public function TsfController()
		{
			super();
			this.initialize();
		}
		
		public function initialize():void
		{
			addCommand(EntrySearchEvent.EVENT_ID, EntrySearchCommand);
			addCommand(LinChuangSearchEvent.EVENT_ID, LinchuangSearchCommand);
			addCommand(RelationSearchEvent.EVENT_ID, RelationSearchCommand);
			addCommand(RecordSearchEvent.EVENT_ID, RecordSearchCommand);
			addCommand(IndexSearchEvent.EVENT_ID, IndexSearchCommand);
			addCommand(BasicSearchEvent.EVENT_ID, BasicSearchCommand);
			addCommand(GeneralSearchEvent.EVENT_ID, GeneralSearchCommand);
			addCommand(StatSearchEvent.EVENT_ID, StatSearchCommand);
			addCommand(LinChuangGeneralSearchEvent.EVENT_ID, LinChuangGeneralSearchCommand);
			addCommand(LinchuangPicSearchEvent.EVENT_ID, LinchuangPicSearchCommand);
		}
		
	}
}