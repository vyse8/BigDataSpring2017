
import java.io.{File, ByteArrayInputStream}
import java.nio.file.{Files, Paths}
import javax.imageio.{ImageWriteParam, IIOImage, ImageIO}
//import java.util.Base64
import sun.misc.BASE64Decoder;
import _root_.unfiltered.request.Body
import _root_.unfiltered.request.Path
import _root_.unfiltered.response.Ok
import _root_.unfiltered.response.ResponseString
import unfiltered.filter.Plan
import unfiltered.jetty.SocketPortBinding
import unfiltered.request._


object SimplePlan extends Plan {
  def intent = {

    case req @ GET(Path("/get")) => {
      Ok ~> ResponseString("word")
    }

    case req @ POST(Path("/get_custom")) => {
      println("There are 7 characters in this word.")
      Ok ~> ResponseString("word")
    }
  }
}
object SimpleServer extends App {
  val bindingIP = SocketPortBinding(host = "192.168.85.41", port = 8080)
  unfiltered.jetty.Server.portBinding(bindingIP).plan(SimplePlan).run()
}
