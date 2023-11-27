import { Course } from "../../entities/Course.ts";
import {
  Button,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import useUpdateCourseForm from "../../hooks/course/useUpdateCourseForm.ts";

interface Props {
  course: Course;
  onSuccess: () => void;
}

const UpdateCourseForm = ({ course, onSuccess }: Props) => {
  const updateCourseForm = useUpdateCourseForm(course, onSuccess);

  return (
    <Stack spacing={4}>
      <FormControl id="fullName">
        <FormLabel>Name</FormLabel>
        <Input
          type="text"
          name="name"
          placeholder="Maths 12 B"
          value={updateCourseForm.values.name}
          onChange={updateCourseForm.handleChange}
        />
        {updateCourseForm.touched.name && updateCourseForm.errors.name && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {updateCourseForm.errors.name}
          </Text>
        )}
      </FormControl>
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!updateCourseForm.dirty || !updateCourseForm.isValid}
        onClick={updateCourseForm.handleSubmit}
      >
        Edit
      </Button>
    </Stack>
  );
};
export default UpdateCourseForm;
