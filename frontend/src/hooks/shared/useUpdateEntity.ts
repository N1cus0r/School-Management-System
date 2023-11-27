import { useMutation, useQueryClient } from "react-query";
import notificationService from "../../services/notification-service.ts";
import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";

const useUpdateEntity = (
  endpoint: string,
  entityName: string,
  CACHE_KEY: any[],
  onSuccess: () => void,
) => {
  const queryClient = useQueryClient();

  const authenticatedApiClient = new AuthenticatedApiClient(endpoint);

  return useMutation({
    mutationFn: ([id, data]) => authenticatedApiClient.put(id, data),
    onSuccess: () => {
      onSuccess();
      notificationService.successNotification(
        "Success",
        `${entityName} edited successfully`,
      );
      queryClient.invalidateQueries({ queryKey: CACHE_KEY });
    },
    onError: (error) =>
      notificationService.errorNotification(
        "Error",
        error.response.data.message,
      ),
  });
};

export default useUpdateEntity;
