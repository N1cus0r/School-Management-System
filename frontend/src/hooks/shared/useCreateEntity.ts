import { useMutation, useQueryClient } from "react-query";
import notificationService from "../../services/notification-service.ts";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";

const useCreateEntity = (
  endpoint: string,
  entityName: string,
  CACHE_KEY: any[],
  onSuccess: () => void,
) => {
  const authenticatedApiClient = new AuthenticatedApiClient(endpoint);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authenticatedApiClient.post,
    onSuccess: () => {
      onSuccess();
      notificationService.successNotification(
        "Success",
        `${entityName} created successfully`,
      );
      queryClient.invalidateQueries({ queryKey: CACHE_KEY });
    },
    onError: (error) => {
      notificationService.errorNotification(
        "Error",
        error.response.data.message,
      );
    },
  });
};

export default useCreateEntity;
