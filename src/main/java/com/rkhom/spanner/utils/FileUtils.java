package com.rkhom.spanner.utils;

import java.io.IOException;
import java.nio.file.Files;
import org.springframework.core.io.ClassPathResource;

public class FileUtils {

  private static final String IO_ERROR = "Cannot read file %s.";

  public static String readFileAsString(String filePath) {
    try {
      return Files.readString(new ClassPathResource(filePath).getFile().toPath());
    } catch (IOException e) {
      throw new IllegalStateException(String.format(IO_ERROR, filePath), e);
    }
  }

}
