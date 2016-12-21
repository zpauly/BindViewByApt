package com.zpauly.compiler;

import com.google.auto.service.AutoService;
import com.zpauly.annotations.BindView;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by zpauly on 2016/12/20.
 */

@AutoService(Processor.class)
public class ViewBindProcessor extends AbstractProcessor {
    private Types types;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.types = processingEnvironment.getTypeUtils();
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        bindView(roundEnvironment);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }

    private void bindView(RoundEnvironment env) {
        Map<TypeElement, Binding> bindingMap = new HashMap<>();

        for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation BindView should be used with field");
            } else {
                int i = 0;
                while (i < 3) {
                    try {
                        addBinding(element, bindingMap).generateJavaFile().writeTo(filer);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        messager.printMessage(Diagnostic.Kind.ERROR, "Error during write java file.");
                        i ++;
                    }
                }
            }
        }
    }

    private Binding addBinding(Element element, Map<TypeElement, Binding> map) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        Binding binding;
        if (map.get(enclosingElement) != null) {
            binding = map.get(enclosingElement);
        } else {
            binding = new Binding(enclosingElement);
            map.put(enclosingElement, binding);
        }
        binding.add(element);
        return binding;
    }
}
