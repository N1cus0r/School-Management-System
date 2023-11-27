import { authenticatedAxiosInstance } from "../../services/authenticated-api-client.ts";
import { useMutation } from "react-query";
import notificationService from "../../services/notification-service.ts";

const useAddStudentToCourse = (courseId: number, onSuccess: () => void) => {
  return useMutation({
    mutationFn: (studentId: number) =>
      authenticatedAxiosInstance
        .put(`/courses/${courseId}/add-student/${studentId}`)
        .then((res) => res.data),
    onSuccess: () => {
      onSuccess();
      notificationService.successNotification(
        "Success",
        "Student added successfully",
      );
    },
    onError: (error) =>
      notificationService.errorNotification(
        "Error",
        error.response.data.message,
      ),
  });
};

export default useAddStudentToCourse;
