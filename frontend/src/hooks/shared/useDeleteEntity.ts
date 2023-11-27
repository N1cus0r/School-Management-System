import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import { useMutation, useQueryClient } from "react-query";

const useDeleteEntity = (
  endpoint: string,
  CACHE_KEY: any[],
  onDelete: () => void,
  onSuccess: () => void,
  onError: () => void,
) => {
  const authenticatedApiClient = new AuthenticatedApiClient(endpoint);
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: authenticatedApiClient.delete,
    onMutate: () => onDelete(),
    onSuccess: () => {
      onSuccess();
      console.log("Invalidating:" + CACHE_KEY);
      queryClient.invalidateQueries({ queryKey: CACHE_KEY });
    },
    onError: onError,
  });
};

export default useDeleteEntity;
