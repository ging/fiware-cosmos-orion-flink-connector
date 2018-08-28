package connector



    case class NgsiEvent( creationTime:Long,
                      fiwareService:String,
                      fiwareServicePath:String,
                      timestamp:Long,
                      entityType:String,
                      entityPattern:String,
                      entityId:String,
                      attrs:Iterable[Attributes],
                      count:Int )