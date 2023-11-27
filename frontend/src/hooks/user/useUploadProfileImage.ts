import { useMutation, useQueryClient } from "react-query";
import notificationService from "../../services/notification-service.ts";
import { authenticatedAxiosInstance } from "../../services/authenticated-api-client.ts";

const useUploadProfileImage = (
  userId: number,
  CACHE_KEY: any[],
  onSuccess: () => void,
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (formData: FormData) =>
      authenticatedAxiosInstance
        .put(`/users/${userId}/profile-image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        })
        .then((res) => res.data),
    onSuccess: () => {
      onSuccess();
      notificationService.successNotification(
        "Success",
        `Profile image updated successfully`,
      );
      queryClient.invalidateQueries({ queryKey: CACHE_KEY });
    },
    onError: (error) => {
      console.log("ERROR");
      notificationService.errorNotification(
        "Error",
        error.response.data.message,
      );
    },
  });
};

export default useUploadProfileImage;
