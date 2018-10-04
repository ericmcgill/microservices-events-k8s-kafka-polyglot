package models
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Metadata(id: String, role: String, cmd: String, contributors: List[String], timestamp: Int)
case class OrderRequest(metadata: Metadata, data: List[String])

object Metadata {
  implicit val metadataWrites = new Writes[Metadata] {
    def writes(m: Metadata) = Json.obj(
      "id" -> m.id,
      "role" -> m.role,
      "cmd" -> m.cmd,
      "contributors" -> m.contributors,
      "timestamp" -> m.timestamp
    )
  }

  implicit val metadataReads: Reads[Metadata] = (
    (__ \ "id").read[String] and
      (__ \ "role").read[String] and
      (__ \ "cmd").read[String] and
      (__ \ "contributors").read[List[String]] and
      (__ \ "timestamp").read[Int]
    )(Metadata.apply _)
}

object OrderRequest {

  implicit val orderRequestWrites = new Writes[OrderRequest] {
    def writes(or: OrderRequest) = Json.obj(
      "metadata" -> or.metadata,
      "data" -> or.data
    )
  }

  implicit val orderRequestReads: Reads[OrderRequest] = (
    (__ \ "metadata").read[Metadata] and
      (__ \ "data").read[List[String]]
    )(OrderRequest.apply _)
}


