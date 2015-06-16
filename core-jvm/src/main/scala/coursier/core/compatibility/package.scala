package coursier.core

package object compatibility {

  type DateTime = org.http4s.DateTime
  val DateTime: org.http4s.DateTime.type = org.http4s.DateTime

  implicit class RichChar(val c: Char) extends AnyVal {
    def letterOrDigit = c.isLetterOrDigit
    def letter = c.isLetter
  }

  def xmlParse(s: String): Either[String, Xml.Node] = {
    def parse =
      try Right(scala.xml.XML.loadString(s))
      catch { case e: Exception => Left(e.getMessage) }

    def fromNode(node: scala.xml.Node): Xml.Node =
      new Xml.Node {
        def label = node.label
        def child = node.child.map(fromNode)
        def isText = node match { case _: scala.xml.Text => true; case _ => false }
        def textContent = node match {
          case t: scala.xml.Text => t.data
          case _ => throw new NoSuchElementException("text of non text node")
        }
        def isElement = node match { case _: scala.xml.Elem => true; case _ => false }
      }

    parse.right
      .map(fromNode)
  }

}
