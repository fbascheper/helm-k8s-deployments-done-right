## Demo 2. Deploy Spring Boot microservice with Helm.


### Part 1. Build and run locally

````shell
cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/
cd ./helm-demo-02-spring-boot-microservice

mvn clean verify
# --> tests complete ok, with H2

mvn spring-boot:run
curl -v localhost:8080/employees
curl -v localhost:8080/employees/1
curl -v localhost:8080/employees/3


curl -X PUT localhost:8080/employees/3 -H 'Content-type:application/json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'
curl -v localhost:8080/employees/3

curl -X DELETE localhost:8080/employees/3
curl -v localhost:8080/employees/3
````

---

### Part 2. Build and run in local Minikube

1. Build docker image & load in minikube
   - Run
     ````shell
     cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
     mvn clean verify
     
     cd ./deploy-scripts/docker
     docker build -t spring-boot-microservice:1.0.0 .
                
     # Load in minikube
     minikube image load spring-boot-microservice:1.0.0
     ````                                              

2. Create a new default Chart using `helm create`:
    - Run
      ````shell
      # Set context to Minikube
      kubectl config current-context
      kubectl config get-contexts
      ###  kubectl config use-context JaxLondon2AKSCluster
      kubectl config use-context minikube
    
      cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
      cd ./deploy-scripts
    
      # Clean up
      rm -rf demo-02-spring-boot-microservice
    
      # Create NEW helm chart
      helm create demo-02-spring-boot-microservice
      ````

3. Modify the Chart with a YAML editor.
   - Update the values to 
      * Use the docker-image `spring-boot-microservice:1.0.0`
      * Expose the `NodePort 8080` from the microservice
   - Edit file `demo-02-spring-boot-microservice/values.yaml` and update
     ````yaml
      image:
        repository: nginx
      service:
          type: ClusterIP
          port: 80
      ````
      to
      ````yaml
      image:
        repository: spring-boot-microservice
      service:
          type: NodePort
          port: 8080
      ````

   - Edit file `demo-02-spring-boot-microservice/Chart.yaml` and update
     ````yaml
     appVersion: "1.16.0"
     ````
     to
     ````yaml
     appVersion: "1.0.0"
     ````
 
4. Deploy the helm hart
    - Run
      ````shell
      cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
      cd ./deploy-scripts
      helm install demo-02-develop ./demo-02-spring-boot-microservice
    
      kubectl get pods
      kubectl get services
      kubectl get deployments
      ````
    - What's wrong? The pod is not running and keeps restarting?
      - We must fix `livenessProbe` and `readinessProbe`
    - Modify `templates/deployment.yaml` and change
      ````yaml
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8081
            initialDelaySeconds: 10
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8081
            initialDelaySeconds: 10
            periodSeconds: 10
      ````
   - Run
     ````shell
     cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
     cd ./deploy-scripts
     helm ls
     helm upgrade demo-02-develop ./demo-02-spring-boot-microservice
     helm ls
     ````

   - Still not running! What's wrong? 
     - We must fix the application, it doesn't expose the management ports
     - Update `application.yaml`and add
       ````yaml
       management:
         health:
            livenessState:
              enabled: true
            readinessState:
              enabled: true
         server:
           port: 8081
         endpoint:
           health:
             enabled: true
             probes:
               enabled: true
           info:
             enabled: true
         endpoints:       
           web:
             exposure:
               include: "*"
               exclude: "env,beans"
       ````
     - Rebuild the docker image
     - Run
       ````shell
       cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
       mvn clean verify
       mvn spring-boot:run
       curl -v localhost:8081/actuator/health/liveness
       curl -v localhost:8081/actuator/health/readiness

       # Build with new tag (1.0.1)     
       cd ./deploy-scripts/docker
       docker build -t spring-boot-microservice:1.0.1 .
                
       # Load in minikube
       minikube image load spring-boot-microservice:1.0.1
       ````                                              
     - Edit file `demo-02-spring-boot-microservice/Chart.yaml` and update
       ````yaml
       appVersion: "1.0.1"
       ````
     - Run
       ````shell
       cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/helm-demo-02-spring-boot-microservice/
       cd ./deploy-scripts
       helm upgrade demo-02-develop ./demo-02-spring-boot-microservice
       helm ls
       
       kubectl get pods
       ````
       * Now pods are ready AND running !
       * Test locally --> WORKS !

---

### Part 3. Add postgresql database
> Purpose: show more complex things possible with Helm charts

1. Update Chart.yaml: add Postgres dependencies to the chart
     - Modify `Chart.yaml` and add
      ````yaml
      dependencies:                                       
         - name: postgresql
           version: 13.0.0
           repository: https://charts.bitnami.com/bitnami
      ````

2. Update values.yaml: add Postgres values + Volume and volume-mounts 
    - Modify `values.yaml` and add
      ````yaml
      global:
        postgresql:
          auth:
            postgresPassword: "Hello123"
            username: "mydbPguser"
            password: "Hello123"
            database: "empDb"
          service:
            ports:
              postgresql: "5432"
      volumes: [
        {
           name: "application-config-volume",
           configMap: {
             name: "application-config-map"
           }
         }
       ]
      volumeMounts: [
        {
          "name": "application-config-volume",
          "mountPath": "/config",
          "readOnly": true
        }
      ]       
      ````

3. Add configmap.yaml and application-template files
    - Create a `configmap.yaml` and add application-template.txt (see GIT)
      ````shell
      # Verify with dry-run command. 
      helm install --dry-run --debug demo-02-develop ./demo-02-spring-boot-microservice
          
      helm upgrade debug demo-02-develop ./demo-02-spring-boot-microservice
      
      kubectl get pods
      kubectl exec -it demo-02-develop-demo-02-spring-boot-microservice-6dd479bbb9fxqh -- bash
      kubectl logs -f  demo-02-develop-demo-02-spring-boot-microservice-6dd479bbb9fxqh
      kubectl exec 
      ````

