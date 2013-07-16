package events
{
	import flash.events.Event;

	public class ShowRecordEvent extends Event
	{
		public static const SHOW_RECORD:String = "showRecord";
		public var index:int;
		
		public function ShowRecordEvent(type:String, index:int)
		{
			super(type);
			this.index = index;
		}
		
		override public function clone():Event
		{
			return new ShowRecordEvent(type, index);
		}
		
	}
}