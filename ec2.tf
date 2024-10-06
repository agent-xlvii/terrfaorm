# EC2 Instance
resource "aws_instance" "example" {
  ami             = "ami-0fff1b9a61dec8a5f"
  instance_type   = "t2.micro"
  tags = {
  Name = "ExampleInstance"
  }
}