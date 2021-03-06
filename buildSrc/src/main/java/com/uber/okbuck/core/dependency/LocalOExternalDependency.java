package com.uber.okbuck.core.dependency;

import static com.uber.okbuck.core.dependency.OResolvedDependency.AAR;
import static com.uber.okbuck.core.dependency.OResolvedDependency.JAR;

import com.google.common.collect.ImmutableList;
import com.uber.okbuck.core.util.ProjectUtil;
import com.uber.okbuck.extension.ExternalDependenciesExtension;
import com.uber.okbuck.extension.JetifierExtension;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import org.gradle.api.Project;

public final class LocalOExternalDependency extends OExternalDependency {

  @Nullable
  Path computeSourceFile(Project project) {
    if (!DependencyUtils.isWhiteListed(getRealDependencyFile())
        && ImmutableList.of(JAR, AAR).contains(getPackaging())) {

      String sourceFileName = getSourceFileNameFrom(getRealDependencyFile().getName());
      Path sourcesJar = getRealDependencyFile().getParentFile().toPath().resolve(sourceFileName);

      if (Files.exists(sourcesJar)) {
        return sourcesJar;
      }
    }
    return null;
  }

  /** Gets the real sources jar file for the dependency. */
  @Override
  public Optional<File> getRealSourceFile() {
    Optional<File> optionalSourceFile = super.getRealSourceFile();

    if (!optionalSourceFile.isPresent() && ProjectUtil.canHaveSources(getRealDependencyFile())) {
      String sourceFileName = getSourceFileNameFrom(getRealDependencyFile().getName());
      Path sourcesJar = getRealDependencyFile().getParentFile().toPath().resolve(sourceFileName);

      if (Files.exists(sourcesJar)) {
        return Optional.of(sourcesJar.toFile());
      }
    }
    return optionalSourceFile;
  }

  LocalOExternalDependency(
      OResolvedDependency resolvedDependency,
      ExternalDependenciesExtension externalDependenciesExtension,
      JetifierExtension jetifierExtension) {
    super(resolvedDependency, externalDependenciesExtension, jetifierExtension);
  }
}
