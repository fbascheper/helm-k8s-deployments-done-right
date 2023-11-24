## Demo 1. Deploy NGINX with helm.

1. Deploy the default Chart created by `helm create`:

    ````shell
    cd $HOME/work/conferences/2023-10-03-Jax-London-Helm-K8S-deployments-done-right/
    cd ./helm-demo-01-nginx/deploy-scripts
    
    # Clean up
    rm -rf demo-01-nginx
    
    # Create NEW helm chart
    helm create demo-01-nginx
    vi demo-01-nginx/Chart.yaml
    
    # Set context to Azure AKS
    kubectl config current-context
    kubectl config get-contexts
    kubectl config use-context JaxLondon2AKSCluster
    
    helm install demo-01-develop ./demo-01-nginx
    ## ---
    ## Note: this is a short-hand for 
    ## helm install -f ./demo-01-nginx/values.yaml demo-01-develop ./demo-01-nginx
    ## ---
    ````
    
    #### Mind the NOTES on screen (from NOTES.txt inside the helm chart)
    > The application can be accessed only by port-forwarding !
    
    ````shell
       export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=demo-01-nginx,app.kubernetes.io/instance=demo-01-develop" -o jsonpath="{.items[0].metadata.name}")
       export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
       echo "Visit http://127.0.0.1:8080 to use your application"
       kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
    ````

2. Modify default Chart: 
   - We want to make the application available on the internet (port 80)
   - Edit file `demo-01-nginx/values.yaml` and update 
     ````yaml
      service:
          type: ClusterIP
          port: 80
      ````
      to  
     ````yaml
      service:
          type: LoadBalancer
          port: 80
      ````

3. Upgrade the release to a new version of the chart:
   - Run 
     ````shell
     helm ls
     helm upgrade demo-01-develop ./demo-01-nginx
     helm ls
     helm rollback demo-01-develop 1
     ````
4. Uninstall the release
   ````shell
   helm uninstall demo-01-develop
   ````


