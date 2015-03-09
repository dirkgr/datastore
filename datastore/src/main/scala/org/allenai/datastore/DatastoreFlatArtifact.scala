package org.allenai.datastore

import java.nio.file.Files

import org.allenai.pipeline.{ ArtifactStreamWriter, FlatArtifact }
import org.allenai.common.Resource

class DatastoreFlatArtifact(
    datastore: Datastore,
    group: String,
    name: String,
    version: Int
) extends FlatArtifact {

  def exists: Boolean = datastore.fileExists(group, name, version)

  def url: java.net.URI = ???

  def read: java.io.InputStream = Files.newInputStream(datastore.filePath(group, name, version))

  def write[T](writer: org.allenai.pipeline.ArtifactStreamWriter => T): T = {
    val tmpFile = Files.createTempFile(this.getClass.getSimpleName, ".tmp")
    tmpFile.toFile.deleteOnExit()
    val result = Resource.using(Files.newOutputStream(tmpFile)) { os =>
      writer(new ArtifactStreamWriter(os))
    }

    datastore.publishFile(tmpFile, group, name, version, false)
    Files.deleteIfExists(tmpFile)

    result
  }
}
