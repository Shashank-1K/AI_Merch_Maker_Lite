const Jimp = require('jimp');
const path = require('path');

// Get the absolute artwork path passed directly from the Java orchestrator
const artworkPath = process.argv[2];

if (!artworkPath) {
    console.error("Error: No artwork path provided from orchestrator.");
    console.log(JSON.stringify({ status: 'error', message: 'No artwork path provided' }));
    process.exit(1);
}

async function createMockup() {
    try {
        console.error("Creating mockup...");

        const TSHIRT_TEMPLATE_PATH = path.join(__dirname, 'tshirt_template.png');
        const OUTPUT_DIR = path.join(__dirname, '../generated_products');

        const [template, artwork] = await Promise.all([
            Jimp.read(TSHIRT_TEMPLATE_PATH),
            Jimp.read(artworkPath)
        ]);
        
        // (Optional) You can remove the debug logs now if you wish
        console.error(`DEBUG: Template dimensions: ${template.getWidth()}x${template.getHeight()}`);
        console.error(`DEBUG: Artwork dimensions: ${artwork.getWidth()}x${artwork.getHeight()}`);

        
        // --- Resize the artwork to fit on the T-shirt ---
        artwork.resize(300, Jimp.AUTO); // Resize artwork to 350px width, auto height
        console.error(`DEBUG: Resized artwork dimensions: ${artwork.getWidth()}x${artwork.getHeight()}`);
        // ------

        // Center the resized artwork
        const positionX = (template.getWidth() - artwork.getWidth()) / 2;
        const positionY = (template.getHeight() - artwork.getHeight()) / 2;
        template.composite(artwork, positionX, positionY);

        const artworkFilename = path.basename(artworkPath);
        const mockupFilename = artworkFilename.replace(/_artwork\.png$/i, '_mockup.png');
        const mockupPath = path.join(OUTPUT_DIR, mockupFilename);
        const relativeMockupPath = path.join('generated_products', mockupFilename);

        await template.writeAsync(mockupPath);
        console.error(`Mockup saved to: ${mockupPath}`);

        const mockupResponse = {
            status: 'success',
            mockup_url: mockupPath.replace(/\\/g, '\\\\'),
            print_files: [
                { type: 'front_print', url: artworkPath.replace(/\\/g, '\\\\') },
                { type: 'mockup_image', url: relativeMockupPath.replace(/\\/g, '\\\\') }
            ],
            generation_timestamp: new Date().toISOString(),
        };

        console.log(JSON.stringify(mockupResponse));

    } catch (error) {
        console.error(`Error creating mockup: ${error.message}`);
        console.log(JSON.stringify({ status: 'error', message: error.message }));
    }
}

// Running the main function
createMockup();