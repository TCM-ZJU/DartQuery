package events
{
	import flash.events.Event;

	public class ShowStatisticsEvent extends Event
	{
		public static const SHOW_STATISTICS:String = "showStatistics";
		public var index:int;
		
		public function ShowStatisticsEvent(type:String, index:int)
		{
			super(type);
			this.index = index;
		}
		
		override public function clone():Event
		{
			return new ShowStatisticsEvent(type, index);
		}
		
	}
}