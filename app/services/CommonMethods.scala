package services

object CommonMethods {
  def getTotalPages(total: Int, limit: Option[Int]) = {
    if (total != 0) {
      val lim = limit match {
        case Some(v) => v
        case None => total
      }
      (total % lim) match {
        case 0 => total / lim
        case _ => (total / lim) + 1
      }
    } else {
      1
    }
  }

}


