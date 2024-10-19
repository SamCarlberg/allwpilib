import javax.annotation.processing.Processor;

module wpilib.epilogue.processor {
  requires java.compiler;
  requires wpilib.epilogue;

  provides Processor with edu.wpi.first.epilogue.processor.AnnotationProcessor;
}
