import useDecodedAccessToken from "../../hooks/auth/useDecodedAccessToken.ts";
import useProfileData from "../../hooks/user/useProfileData.ts";
import { Navigate } from "react-router-dom";
import {
  Button,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import useCreateCourseForm from "../../hooks/course/useCreateCourseForm.ts";
import { Role } from "../../entities/user/Role.ts";
import CourseTeacherSelect from "./CourseTeacherSelect.tsx";
import MainSelect from "./test-select/MainSelect.tsx";
import TestSelect from "./test-select/TestSelect.tsx";

interface Props {
  onSuccess: () => void;
}

const CreateCourseForm = ({ onSuccess }: Props) => {
  const token = useDecodedAccessToken();
  const { data: user } = useProfileData(token.sub);

  if (!user) {
    return <Navigate to={"/login"} />;
  }

  const createCourseForm = useCreateCourseForm(user, onSuccess);

  return (
    <div>
      <Stack spacing={4}>
        <FormControl id="fullName">
          <FormLabel>Name</FormLabel>
          <Input
            type="text"
            name="name"
            placeholder="Maths 12 B"
            value={createCourseForm.values.name}
            onChange={createCourseForm.handleChange}
          />
          {createCourseForm.touched.name && createCourseForm.errors.name && (
            <Text color="red.500" fontSize="sm" mt={1}>
              {createCourseForm.errors.name}
            </Text>
          )}
        </FormControl>
        {user.role === Role.ADMIN && (
          <FormControl id="gender">
            <FormLabel>Teacher</FormLabel>
            <CourseTeacherSelect
              value={createCourseForm.values.teacherId}
              onChange={createCourseForm.handleChange}
            />
            {createCourseForm.touched.teacherId &&
              createCourseForm.errors.teacherId && (
                <Text color="red.500" fontSize="sm" mt={1}>
                  {createCourseForm.errors.teacherId}
                </Text>
              )}
          </FormControl>
        )}
        <Button
          colorScheme={"blue"}
          variant={"solid"}
          isDisabled={!createCourseForm.dirty || !createCourseForm.isValid}
          onClick={createCourseForm.handleSubmit}
        >
          Create
        </Button>
      </Stack>
    </div>
  );
};
export default CreateCourseForm;
