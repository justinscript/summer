/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.ms.commons.test.BaseTestCase;
import com.ms.commons.test.annotation.TestCaseInfo;
import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.FileUtil;
import com.ms.commons.test.common.MessyCodeUtil;
import com.ms.commons.test.runner.filter.expression.internal.AbstractSimpleExpression;
import com.ms.commons.test.runner.filter.expression.internal.builder.SimpleExpressionBuiler;
import com.ms.commons.test.runner.filter.expression.internal.exception.ParseException;
import com.ms.commons.test.runner.filter.expression.util.ExpressionParseUtil;

/**
 * @author zxc Apr 13, 2013 11:42:58 PM
 */
public class GenerateTestCase {

    private static class ProjectPath {

        private String mainSource;
        private String testSource;

        public ProjectPath(String mainSource, String testSource) {
            this.mainSource = mainSource;
            this.testSource = testSource;
        }

        public String getMainSource() {
            return mainSource;
        }

        public String getTestSource() {
            return testSource;
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private static class MethodFieldVisitor extends VoidVisitorAdapter {

        private List<MethodDeclaration> methodList;
        private List<FieldDeclaration>  fieldList;

        public MethodFieldVisitor(List<MethodDeclaration> methodList, List<FieldDeclaration> fieldList) {
            this.methodList = methodList;
            this.fieldList = fieldList;
        }

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            methodList.add(n);
        }

        public void visit(FieldDeclaration n, Object arg) {
            fieldList.add(n);
        }
    }

    private static interface FullClassNameFilter {

        boolean accept(String fullClassName);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage:\r\n.frameworktest_maketests antx|maven fileter");
            System.exit(-1);
        }

        final com.ms.commons.test.runner.filter.expression.internal.Expression filterExpression;
        try {
            System.out.println("Filter: " + args[1]);
            filterExpression = ExpressionParseUtil.parse(args[1], new SimpleExpressionBuiler() {

                public AbstractSimpleExpression build(String value) {
                    return new StringExpressionImpl(value);
                }
            });
        } catch (ParseException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
        final FullClassNameFilter fullClassNameFilter = new FullClassNameFilter() {

            public boolean accept(String fullClassName) {

                return ((Boolean) filterExpression.evaluate(fullClassName)).booleanValue();
            }
        };

        String userDir = System.getProperty("user.dir");

        ProjectPath pp = getProjectPath(args[0]);

        final String mainSource = userDir + File.separator + pp.getMainSource();
        final String testSource = userDir + File.separator + pp.getTestSource();

        FileUtil.listFiles(null, new File(mainSource), new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return !pathname.toString().contains(".svn");
                }
                if (pathname.toString().contains(".svn")) {
                    return false;
                }
                if (!pathname.toString().toLowerCase().endsWith(".java")) {
                    return false;
                }
                try {
                    processJavaFile(pathname, testSource, fullClassNameFilter);
                } catch (Exception e) {
                    System.err.println("Parse java file failed:" + pathname);
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private static void processJavaFile(File pathname, String testSource, FullClassNameFilter fullClassNameFilter)
                                                                                                                  throws Exception {
        StringBuilder unitReadEncoding = new StringBuilder();
        CompilationUnit unit = parseCompilationUnit(pathname, unitReadEncoding);

        String packageName = unit.getPackage().getName().toString();
        String className = unit.getTypes().get(0).getName();
        String fullClassName = packageName + "." + className;

        if (!fullClassNameFilter.accept(fullClassName)) {
            System.out.println("Skip: " + fullClassName);
            return;
        }

        String testFullClassName = fullClassName + "Test";

        File testFile = new File(testSource + File.separator + testFullClassName.replace('.', File.separatorChar)
                                 + ".java");

        // filter full class name
        List<MethodDeclaration> needTestMethodList = getNeedTestMethodList(unit);
        List<String> testMethodNameList = convertToStringTestMethodNameList(needTestMethodList);

        CompilationUnit testUnit;
        StringBuilder testUnitReadEncoding = new StringBuilder();
        if (testFile.exists()) {
            testUnit = parseCompilationUnit(testFile, testUnitReadEncoding);
        } else {
            testUnit = makeCompilationUnit(packageName, className + "Test", // -
                                           "Test case for {@link " + fullClassName + "}");
        }
        addImports(testUnit, BASE_IMPORTS);
        List<MethodDeclaration> addedMethods = addMember(testUnit, testMethodNameList);

        if (addedMethods.isEmpty()) {
            System.err.println("No test method exists or no test method added for: " + testFullClassName);
        } else {
            if (testFile.exists()) {
                writeAddedMembersToTestFile(testFile, addedMethods, testUnitReadEncoding);
                System.out.println("Add test method(s) to: " + testFullClassName);
            } else {
                FileUtils.writeStringToFile(testFile, testUnit.toString(), unitReadEncoding.toString());
                System.out.println("Create test case: " + testFullClassName);
            }
        }
    }

    private static void writeAddedMembersToTestFile(File testFile, List<MethodDeclaration> addedMethods,
                                                    StringBuilder testUnitReadEncoding) {
        try {
            String code = readCodeFromFile(testFile, new StringBuilder());

            String codePart1 = code.substring(0, code.lastIndexOf('}'));
            String codePart2 = code.substring(code.lastIndexOf('}'));

            StringWriter sw = new StringWriter();
            PrintWriter newMethodsCode = new PrintWriter(sw);

            for (MethodDeclaration method : addedMethods) {
                newMethodsCode.println();
                String me = method.toString();
                BufferedReader br = new BufferedReader(new StringReader(me));
                for (String l; (l = br.readLine()) != null;) {
                    newMethodsCode.println("    " + l);
                }
            }
            newMethodsCode.flush();

            String newCode = codePart1 + sw + codePart2;

            // check if have syntax error
            JavaParser.parse(convertStringToInputstream(newCode, "UTF-8"), "UTF-8");

            FileUtils.writeStringToFile(testFile, newCode, testUnitReadEncoding.toString());
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static CompilationUnit parseCompilationUnit(File file, StringBuilder outEncoding) {
        try {
            String code = readCodeFromFile(file, outEncoding);
            return JavaParser.parse(convertStringToInputstream(code, "UTF-8"), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream convertStringToInputstream(String string, String encoding) {
        try {
            return new ByteArrayInputStream(string.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static String readCodeFromFile(File file, StringBuilder outEncoding) {
        return readCodeFromFile(file, outEncoding, "GB18030", "UTF-8");
    }

    private static String readCodeFromFile(File file, StringBuilder outEncoding, String defaultEncoding,
                                           String... encodings) {
        try {
            String defaultCode = FileUtils.readFileToString(file, defaultEncoding);

            if (!MessyCodeUtil.hasTestMessyCode(defaultCode)) {
                outEncoding.append(defaultEncoding);
                return defaultCode;
            }

            for (String enc : encodings) {
                String code = FileUtils.readFileToString(file, enc);
                if (!MessyCodeUtil.hasTestMessyCode(code)) {
                    outEncoding.append(enc);
                    return code;
                }
            }

            outEncoding.append(defaultEncoding);
            return defaultCode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> convertToStringTestMethodNameList(List<MethodDeclaration> methodDeclarationList) {
        List<String> methodNameList = new ArrayList<String>();
        for (MethodDeclaration decl : methodDeclarationList) {
            String testName = decl.getName();
            methodNameList.add("test" + testName.substring(0, 1).toUpperCase() + testName.substring(1));
        }
        return methodNameList;
    }

    @SuppressWarnings("unchecked")
    private static List<MethodDeclaration> getNeedTestMethodList(CompilationUnit unit) {
        List<MethodDeclaration> methodList = new ArrayList<MethodDeclaration>();
        List<FieldDeclaration> fieldList = new ArrayList<FieldDeclaration>();
        new MethodFieldVisitor(methodList, fieldList).visit(unit, null);

        return filterMethodForTest(methodList, fieldList);
    }

    private static Set<String> getAllFieldHashSet(List<FieldDeclaration> fieldList) {
        Set<String> fieldSet = new HashSet<String>();
        for (FieldDeclaration f : fieldList) {
            fieldSet.add(f.getVariables().get(0).getId().toString().toLowerCase());
        }
        return fieldSet;
    }

    private static List<MethodDeclaration> filterMethodForTest(List<MethodDeclaration> methodList,
                                                               List<FieldDeclaration> fieldList) {
        Set<String> fieldSet = getAllFieldHashSet(fieldList);
        List<MethodDeclaration> filteredMethodList = new ArrayList<MethodDeclaration>();
        for (MethodDeclaration method : methodList) {
            String name = method.getName();

            if (name.startsWith("get") || name.startsWith("set")) {
                if (fieldSet.contains(name.substring(3).toLowerCase())) {
                    continue;
                }
            }
            filteredMethodList.add(method);
        }
        return filteredMethodList;
    }

    private static ProjectPath getProjectPath(String type) {
        if ("antx".equals(type)) {
            return new ProjectPath("src/java", "src/java.test");
        } else if ("maven".equals(type)) {
            return new ProjectPath("src/main/java", "src/test/java");
        } else {
            throw new RuntimeException("Unkown type: " + type);
        }
    }

    private static final List<String> BASE_IMPORTS = new ArrayList<String>();
    static {
        BASE_IMPORTS.add(Test.class.getName());
        BASE_IMPORTS.add(TestCaseInfo.class.getName());
        BASE_IMPORTS.add(BaseTestCase.class.getName());
    }

    private static void addImports(CompilationUnit unit, List<String> imports) {
        List<ImportDeclaration> unitImports = unit.getImports();
        unitImports = (unitImports == null) ? new ArrayList<ImportDeclaration>() : unitImports;
        Set<String> oldImports = new HashSet<String>();
        for (ImportDeclaration decalaration : unitImports) {
            oldImports.add(decalaration.getName().toString());
        }
        for (String imp : new LinkedHashSet<String>(imports)) {
            if (!oldImports.contains(imp)) {
                unitImports.add(new ImportDeclaration(makeNameExpr(imp), false, false));
            }
        }
        unit.setImports(unitImports);
    }

    private static List<MethodDeclaration> addMember(CompilationUnit unit, List<String> testMethodNames) {
        List<MethodDeclaration> addedMethods = new ArrayList<MethodDeclaration>();

        List<BodyDeclaration> unitMembers = unit.getTypes().get(0).getMembers();
        unitMembers = (unitMembers == null) ? new ArrayList<BodyDeclaration>() : unitMembers;
        Set<String> oldMethods = new HashSet<String>();
        for (BodyDeclaration declaration : unitMembers) {
            if (declaration instanceof MethodDeclaration) {
                oldMethods.add(((MethodDeclaration) declaration).getName().toLowerCase());
            }
        }
        for (String met : new LinkedHashSet<String>(testMethodNames)) {
            if (!oldMethods.contains(met.toLowerCase())) {
                addedMethods.add(makeEmptyTestMethodDeclaration(met));
                unitMembers.add(makeEmptyTestMethodDeclaration(met));
            }
        }
        unit.getTypes().get(0).setMembers(unitMembers);
        return addedMethods;
    }

    private static MethodDeclaration makeEmptyTestMethodDeclaration(String methodName) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.PUBLIC);
        method.setName(methodName);
        method.setAnnotations(Arrays.asList((AnnotationExpr) new MarkerAnnotationExpr(makeNameExpr("Test"))));
        method.setType(new VoidType());

        List<Expression> testMethodArgs = Arrays.asList((Expression) new StringLiteralExpr("To be implement ..."));
        Statement statement = new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType("RuntimeException"),
                                                                   testMethodArgs));
        method.setBody(new BlockStmt(Arrays.asList(statement)));

        return method;
    }

    private static CompilationUnit makeCompilationUnit(String packageName, String testcaseName, String comment) {
        CompilationUnit unit = new CompilationUnit();
        unit.setPackage(new PackageDeclaration(makeNameExpr(packageName)));

        JavadocComment javadocComment = new JavadocComment("\r\n * " + comment + "\r\n ");
        List<ClassOrInterfaceType> testCaseExtends = new ArrayList<ClassOrInterfaceType>();
        testCaseExtends.add(new ClassOrInterfaceType("BaseTestCase"));
        List<BodyDeclaration> members = new ArrayList<BodyDeclaration>();
        ClassOrInterfaceDeclaration clazz = new ClassOrInterfaceDeclaration(javadocComment, Modifier.PUBLIC, null,
                                                                            false, testcaseName, null, testCaseExtends,
                                                                            null, members);
        MemberValuePair mvp = new MemberValuePair(
                                                  "contextKey",
                                                  new StringLiteralExpr(
                                                                        "Your context key or set 'useDataSourceContextKey = true'"));
        clazz.setAnnotations(Arrays.asList((AnnotationExpr) new NormalAnnotationExpr(makeNameExpr("TestCaseInfo"),
                                                                                     Arrays.asList(mvp))));
        unit.setTypes(Arrays.asList((TypeDeclaration) clazz));
        return unit;
    }

    private static NameExpr makeNameExpr(String name) {
        return ASTHelper.createNameExpr(name);
    }
}
