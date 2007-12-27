mvn install:install-file -DgroupId=org.pnuts -DartifactId=pnuts -Dversion=1.1 -Dpackaging=jar -Dfile=pnuts.jar -DcreateChecksum=true -DgeneratePom=true -q

mvn install:install-file -DgroupId=org.pnuts -DartifactId=pnuts-modules -Dversion=1.1 -Dpackaging=jar -Dfile=pnuts-modules.jar -DcreateChecksum=true -DgeneratePom=true -q

mvn install:install-file -DgroupId=org.pnuts -DartifactId=module-tools -Dversion=1.1 -Dpackaging=jar -Dfile=module-tools.jar -DcreateChecksum=true -DgeneratePom=true -q

mvn install:install-file -DgroupId=org.pnuts -DartifactId=functional -Dversion=1.1 -Dpackaging=jar -Dfile=functional.jar -DcreateChecksum=true -DgeneratePom=true -q
