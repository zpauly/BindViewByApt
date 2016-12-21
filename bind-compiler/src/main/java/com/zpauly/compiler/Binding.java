package com.zpauly.compiler;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zpauly.annotations.BindView;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by zpauly on 2016/12/20.
 */

public class Binding {
    private TypeElement sourceElement;
    private Set<Element> targetElementSet = new HashSet<>();

    private TypeSpec.Builder typeSpecBuilder;
    private TypeSpec typeSpec;

    private String packageName;
    private TypeName typeName;
    private ClassName className;
    private boolean isFinal;

    public Binding(TypeElement typeElement) {
        this.sourceElement = typeElement;
    }

    public TypeElement getSourceElement() {
        return sourceElement;
    }

    public void add(Element element) {
        this.targetElementSet.add(element);
    }

    public Set<Element> getTargetElementSet() {
        return targetElementSet;
    }

    public JavaFile generateJavaFile() {
        prepareBinding();

        this.typeSpecBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("com.zpauly.bind", "ViewBinder"))
                .addField(typeName, "target", Modifier.PRIVATE);
        if (isFinal) {
            this.typeSpecBuilder.addModifiers(Modifier.FINAL);
        }

        generateCode();

        return JavaFile.builder(packageName, typeSpec)
                .build();
    }

    private void generateCode() {
        for (Element targetElement : targetElementSet) {
            int id = targetElement.getAnnotation(BindView.class).value();
            typeSpecBuilder.addField(TypeName.get(targetElement.asType()), "view" + id, Modifier.PRIVATE);
        }

        typeSpec = typeSpecBuilder
                .addMethod(createBindingMethod())
                .build();
    }

    private MethodSpec createBindingMethod() {
        MethodSpec.Builder bindViewBuilder = MethodSpec.methodBuilder("bindView")
                .addAnnotation(ClassName.get("android.support.annotation", "UiThread"))
                .addParameter(typeName, "target", Modifier.FINAL)
                .addParameter(ClassName.get("com.zpauly.bind", "Source"), "source", Modifier.FINAL)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.target = target");

        for (Element targetElement : targetElementSet) {
            int id = targetElement.getAnnotation(BindView.class).value();
            bindViewBuilder.addStatement("this.target.$L = ($T) source.findViewById($L);",
                    targetElement.getSimpleName(),
                    ClassName.get(targetElement.asType()),
                    id);
        }
        return bindViewBuilder.build();
    }

    private void prepareBinding() {
        typeName = TypeName.get(sourceElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            typeName = ((ParameterizedTypeName) typeName).rawType;
        }

        this.packageName = MoreElements.getPackage(sourceElement).getQualifiedName().toString();
        String className = sourceElement.getQualifiedName().toString().substring(packageName.length() + 1)
                .replace('.', '$');
        this.className = ClassName.get(packageName, className + "_Binding");

        this.isFinal = sourceElement.getModifiers().contains(Modifier.FINAL);
    }
}
