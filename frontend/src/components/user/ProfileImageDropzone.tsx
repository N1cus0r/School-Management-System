import {useDropzone} from "react-dropzone";
import {useCallback} from "react";
import {Box} from "@chakra-ui/react";
import useUploadProfileImage from "../../hooks/user/useUploadProfileImage.ts";
import {User} from "../../entities/user/User.ts";

interface Props {
  user: User;
  onSuccess: () => void;
}

const ProfileImageDropzone = ({ user, onSuccess }: Props) => {
  const uploadProfileImage = useUploadProfileImage(
    user.id,
    ["user", user.email, "profileImage"],
    onSuccess,
  );

  const onDrop = useCallback((acceptedFiles: File[]) => {
    const formData = new FormData();
    formData.append("file", acceptedFiles[0]);
    uploadProfileImage.mutate(formData);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  return (
    <Box
      {...getRootProps()}
      w={"100%"}
      textAlign={"center"}
      border={"dashed"}
      borderColor={"gray.200"}
      borderRadius={"3xl"}
      p={6}
      rounded={"md"}
    >
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>Drop the picture here ...</p>
      ) : (
        <p>Drag 'n' drop picture here, or click to select picture</p>
      )}
    </Box>
  );
};
export default ProfileImageDropzone;
