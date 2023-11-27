import { createStandaloneToast } from "@chakra-ui/react";

const { toast } = createStandaloneToast();

class NotificationService {
  private static dispatchNotification = (
    title: string,
    description: string,
    status?: "info" | "warning" | "success" | "error" | "loading",
  ) => {
    toast({
      title,
      description,
      status,
      isClosable: true,
      duration: 4000,
    });
  };

  static errorNotification = (title: string, description: string) => {
    this.dispatchNotification(title, description, "error");
  };

  static successNotification = (title: string, description: string) => {
    this.dispatchNotification(title, description, "success");
  };
}

export default NotificationService
