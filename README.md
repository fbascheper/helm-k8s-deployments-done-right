# Helm demo with Azure   
> See [Azure CLI docs](https://learn.microsoft.com/en-us/azure/aks/quickstart-helm?tabs=azure-cli)

---

## 1.  Install helm

Open iTerm2 and run the following commands:

````shell
brew update && brew install helm
brew update && brew install azure-cli
````

---

## 2. Install MiniKube
> See [https://devopscube.com/minikube-mac/](https://devopscube.com/minikube-mac/)

Open iTerm2 and run the following commands:

````shell
# brew install qemu
# brew install socket_vmnet
# brew tap homebrew/services
# HOMEBREW=$(which brew) && sudo ${HOMEBREW} services start socket_vmnet
# brew install minikube

## Start minikube with socket_vmnet driver
minikube start --driver qemu --network socket_vmnet

minikube status
````

---

## 3. Prepare Azure environment
> Open portal.azure.com

````shell

# ----------------- 
###  STEP 1 --- Create an Azure Container Registry
# ----------------- 
  
az login
az account list-locations

az account set --subscription "xxxxxxxx"
az group create --name JaxLondon2-helm-ResourceGroup --location westeurope
az acr create --resource-group JaxLondon2-helm-ResourceGroup --name jax2helmacr --sku Basic


# ----------------- 
# STEP 2 --- Create an AKS Cluster
# ----------------- 

az aks create --resource-group JaxLondon2-helm-ResourceGroup --name JaxLondon2AKSCluster --location westeurope --attach-acr jax2helmacr --generate-ssh-keys


# ----------------- 
# STEP 3 --- Configure kubectl for the AKS Cluster (connect)
# ----------------- 
az aks get-credentials --resource-group JaxLondon2-helm-ResourceGroup --name JaxLondon2AKSCluster


kubectl config current-context
kubectl config get-contexts
````

---

## 3. Run demos

1. Run [helm-demo-01](helm-demo-01-nginx/README.md)
2. Run [helm-demo-02](helm-demo-02-spring-boot-microservice/README.md)


---

###
### Clean-up
###
az aks delete --yes --resource-group JaxLondon2-helm-ResourceGroup --name JaxLondon2AKSCluster
az acr delete --yes --resource-group JaxLondon2-helm-ResourceGroup --name jax2helmacr
az group delete --yes --name JaxLondon2-helm-ResourceGroup

````  


