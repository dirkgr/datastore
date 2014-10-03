package org.allenai.datastore.cli

import org.allenai.datastore.Datastore

import java.io.File

object UploadApp extends App {
  case class Config(
    path: File = null,
    override val group: String = null,
    override val name: String = null,
    override val version: Int = -1,
    override val datastore: Datastore = Datastore,
    overwrite: Boolean = false) extends LocatorConfig

  val parser = new scopt.OptionParser[Config]("scopt") {
    opt[File]('p', "path") required () action { (p, c) =>
      c.copy(path = p)
    } text ("Path to the file or directory you want uploaded")

    opt[String]('g', "group") required () action { (g, c) =>
      c.copy(group = g)
    } text ("Group name to store the file or directory under")

    opt[String]('n', "name") required () action { (n, c) =>
      c.copy(name = n)
    } text ("Name to store the file or directory under")

    opt[Int]('v', "version") required () action { (v, c) =>
      c.copy(version = v)
    } text ("Version number to store the file or directory under")

    opt[String]('d', "datastore") action { (d, c) =>
      c.copy(datastore = new Datastore(d))
    } text ("Datastore to use")

    opt[Boolean]("overwrite") action { (_, c) =>
      c.copy(overwrite = true)
    } text ("Overwrite if the group, version, and name already exists")

    help("help")
  }

  parser.parse(args, Config()) foreach { config =>
    if (config.path.isDirectory) {
      config.datastore.publishDirectory(
        config.path.toPath,
        config.locator,
        config.overwrite)
    } else {
      config.datastore.publishFile(
        config.path.toPath,
        config.locator,
        config.overwrite)
    }
  }
}
