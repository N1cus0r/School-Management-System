import { useMutation } from "react-query";
import { authenticatedAxiosInstance } from "../../services/authenticated-api-client.ts";
import notificationService from "../../services/notification-service.ts";

const useRemoveStudentFromCourse = (
  courseId: number,
  onSuccess: () => void,
) => {
  return useMutation({
    mutationFn: (studentId: number) =>
      authenticatedAxiosInstance
        .delete(`/courses/${courseId}/remove-student/${studentId}`)
        .then((res) => res.data),
    onSuccess: () => {
      onSuccess();
      notificationService.successNotification(
        "Success",
        "Student removed successfully",
      );
    },
    onError: (error) =>
      notificationService.errorNotification(
        "Error",
        error.response.data.message,
      ),
  });
};

export default useRemoveStudentFromCourse
