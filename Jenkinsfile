// ==============================================================================
//  SISTEMA DE INVENTARIO TRANSACCIONAL — PIPELINE CI/CD LOCAL
//  Archivo: Jenkinsfile (Pipeline Declarativo)
//  Requisito: Jenkins instalado localmente con Maven y JDK 17 configurados.
//
//  PASOS PREVIOS EN JENKINS (Global Tool Configuration):
//  ► Añadir una instalación de Maven con el nombre: "Maven 3"
//  ► Añadir una instalación de JDK con el nombre: "Java 17"
//  ► (Opcional) Añadir SonarQube Scanner con el nombre: "SonarScanner"
// ==============================================================================

pipeline {
    agent any

    // ── Herramientas que Jenkins resolverá automáticamente ──────────────────
    tools {
        // ► AJUSTAR: El nombre debe coincidir EXACTAMENTE con el que
        //   configuraste en Jenkins > Global Tool Configuration > Maven.
        maven 'Maven 3'

        // ► AJUSTAR: El nombre debe coincidir con tu instalación de JDK 17.
        jdk 'Java 17'
    }

    // ── Variables de entorno globales del pipeline ──────────────────────────
    environment {
        // Nombre del artefacto JAR que genera Maven (sin la versión).
        APP_NAME = 'sistema-inventario'

        // ► AJUSTAR: Versión de tu proyecto definida en pom.xml.
        APP_VERSION = '1.0.0'

        // Ruta completa al JAR compilado (construida automáticamente).
        JAR_PATH = "target/${APP_NAME}-${APP_VERSION}.jar"

        // ── Configuración SonarQube (Stage 2) ──────────────────────────────
        // ► AJUSTAR: URL de tu servidor SonarQube local (por defecto: 9000).
        SONAR_HOST_URL = 'http://localhost:9000'

        // ► AJUSTAR: Token generado en SonarQube > My Account > Security.
        SONAR_AUTH_TOKEN = credentials('sonar-token-local')

        // ── Configuración Checkmarx (Stage 2) ──────────────────────────────
        // ► AJUSTAR: Ruta de instalación del CLI de Checkmarx en tu máquina.
        CHECKMARX_CLI = 'C:\\CxConsole\\CxConsolePlugin.bat'
    }

    stages {

        // ── STAGE 1: Checkout ──────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo '================================================================'
                echo ' STAGE 1 — Descargando código fuente desde el repositorio Git'
                echo '================================================================'
                // Jenkins clona/actualiza el repo configurado en la pipeline.
                // ► AJUSTAR: Asegúrate de que la Pipeline apunte al repo
                //   correcto en: Pipeline > Definition > SCM > Repository URL.
                checkout scm
            }
        }

        // ── STAGE 2: Build & Test ──────────────────────────────────────────
        stage('Build & Test') {
            steps {
                echo '================================================================'
                echo ' STAGE 2 — Compilando código y ejecutando pruebas unitarias'
                echo '================================================================'

                // "clean test" compila el proyecto y ejecuta TODAS las pruebas
                // de JUnit/Mockito. Si alguna falla, el pipeline se detiene aquí.
                // Se usa 'bat' por ser un entorno Windows local.
                // ► En Linux/Mac reemplaza 'bat' por 'sh'.
                bat 'mvn clean test --batch-mode'
            }
            post {
                // Publica el reporte de pruebas JUnit en la interfaz de Jenkins
                // independientemente de si el stage pasó o falló.
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        // ── STAGE 3: Security & Quality Scan ──────────────────────────────
        stage('Security & Quality Scan') {
            steps {
                echo '================================================================'
                echo ' STAGE 3 — Análisis de calidad (SonarQube) y seguridad (Checkmarx)'
                echo '================================================================'

                // ── 3a. SONARQUBE — Análisis de calidad de código ────────────
                // Requiere: Plugin "SonarQube Scanner for Jenkins" instalado.
                // ► AJUSTAR: Reemplaza "SonarQube-Local" con el nombre del servidor
                //   configurado en Jenkins > Configure System > SonarQube Servers.
                withSonarQubeEnv('SonarQube-Local') {
                    bat """
                        mvn sonar:sonar ^
                            -Dsonar.projectKey=${APP_NAME} ^
                            -Dsonar.projectName="Sistema Inventario" ^
                            -Dsonar.host.url=${SONAR_HOST_URL} ^
                            -Dsonar.token=${SONAR_AUTH_TOKEN} ^
                            -Dsonar.java.coveragePlugin=jacoco ^
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                            --batch-mode
                    """
                }

                // Espera el resultado del Quality Gate de SonarQube.
                // Si la cobertura es < 80% o hay issues críticos, el pipeline falla.
                // ► AJUSTAR: Comenta este bloque si no tienes SonarQube configurado.
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }

                // ── 3b. CHECKMARX — Análisis de vulnerabilidades de seguridad ──
                // Requiere: Plugin "Checkmarx" instalado en Jenkins O el CLI local.
                // ► AJUSTAR: Modifica las rutas y credenciales según tu instalación.
                //   Si no tienes Checkmarx, comenta el bloque step() de abajo.
                echo '--- Iniciando escaneo de seguridad con Checkmarx ---'
                script {
                    // Verifica si el CLI de Checkmarx existe antes de ejecutar.
                    def checkmarxExists = fileExists(env.CHECKMARX_CLI)
                    if (checkmarxExists) {
                        bat """
                            "${CHECKMARX_CLI}" Scan ^
                                -v ^
                                -CxServer http://localhost ^
                                -CxUser admin ^
                                -CxPassword admin123 ^
                                -ProjectName "SP//sistema-inventario" ^
                                -LocationType folder ^
                                -LocationPath ${WORKSPACE}\\src ^
                                -preset "Default 2014" ^
                                -ReportXML target/checkmarx-report.xml
                        """
                        // ► AJUSTAR: Cambia -CxUser y -CxPassword por tus credenciales.
                    } else {
                        echo 'AVISO: CLI de Checkmarx no encontrado en la ruta configurada.'
                        echo 'Saltando escaneo de seguridad. Configura CHECKMARX_CLI para activarlo.'
                    }
                }
            }
        }

        // ── STAGE 4: Package ───────────────────────────────────────────────
        stage('Package') {
            steps {
                echo '================================================================'
                echo ' STAGE 4 — Empaquetando artefacto ejecutable (.jar)'
                echo '================================================================'

                // Genera el JAR final. Se omiten las pruebas porque ya se
                // ejecutaron en el Stage 2 (evita doble ejecución).
                bat 'mvn package -DskipTests --batch-mode'

                echo "Artefacto generado: ${JAR_PATH}"
            }
            post {
                success {
                    // Archiva el JAR para que esté disponible en Jenkins
                    // como artefacto descargable del build.
                    archiveArtifacts artifacts: 'target/*.jar',
                                     fingerprint: true,
                                     allowEmptyArchive: false
                }
            }
        }

        // ── STAGE 5: Deploy Local ──────────────────────────────────────────
        stage('Deploy Local') {
            steps {
                echo '================================================================'
                echo ' STAGE 5 — Desplegando aplicación en servidor local (localhost:8080)'
                echo '================================================================'

                script {
                    // Detiene cualquier instancia previa de la aplicación
                    // que pueda estar corriendo en el puerto 8080.
                    echo 'Buscando y deteniendo instancia previa en puerto 8080...'
                    bat '''
                        FOR /F "tokens=5" %%P IN ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') DO (
                            echo Deteniendo proceso con PID %%P
                            taskkill /F /PID %%P 2>NUL || echo No habia proceso previo en el puerto 8080.
                        )
                    '''

                    // Inicia la aplicación Spring Boot como proceso en segundo plano.
                    // El flag /B evita que abra una nueva ventana de consola.
                    // Las variables de BD se pasan como argumentos para sobreescribir
                    // los valores de application.properties si es necesario.
                    //
                    // ► AJUSTAR: Cambia --spring.datasource.password=root
                    //   por la contraseña real de tu MySQL si es diferente.
                    echo "Iniciando ${APP_NAME} v${APP_VERSION} en segundo plano..."
                    bat """
                        start /B java -jar ${JAR_PATH} ^
                            --server.port=8080 ^
                            --spring.datasource.url=jdbc:mysql://localhost:3306/inventario_db?createDatabaseIfNotExist=true^&useSSL=false^&serverTimezone=UTC ^
                            --spring.datasource.username=root ^
                            --spring.datasource.password=root ^
                            > logs\\app.log 2>&1
                    """

                    // Espera unos segundos para que Spring Boot termine de arrancar.
                    sleep(time: 20, unit: 'SECONDS')

                    // Verifica que la aplicación respondió correctamente en el puerto 8080.
                    echo 'Verificando que la aplicación responde en http://localhost:8080 ...'
                    bat '''
                        curl -f http://localhost:8080/inventario || (
                            echo ERROR: La aplicacion no respondio en http://localhost:8080
                            exit /b 1
                        )
                    '''

                    echo '✅ Aplicación desplegada y respondiendo en http://localhost:8080/inventario'
                }
            }
        }
    }

    // ── POST: Acciones que se ejecutan SIEMPRE al finalizar el pipeline ────
    post {
        always {
            echo '================================================================'
            echo ' POST-PIPELINE — Limpieza y notificación de resultados'
            echo '================================================================'
            echo "Estado final del build: ${currentBuild.currentResult}"
        }
        success {
            echo '✅ Pipeline CI/CD completado EXITOSAMENTE.'
            echo "La aplicación está disponible en: http://localhost:8080/inventario"
        }
        failure {
            echo '❌ El pipeline ha FALLADO. Revisa la consola de Jenkins para más detalles.'
            echo 'Causas comunes: test fallido, Quality Gate rechazado, BD no disponible.'
        }
        unstable {
            echo '⚠️ El pipeline terminó en estado INESTABLE (algunas pruebas fallaron).'
        }
    }
}
